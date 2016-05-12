package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Egor Gorbunov
 * @since 09.05.16
 */


public class ThreadPoolTest {

    private class UselessTask<T> implements Supplier<T> {
        private final String name;
        private final T result;
        private final int sleepMs;

        UselessTask(String name, T result, int sleepMs) {
            this.name = name;
            this.result = result;
            this.sleepMs = sleepMs;
        }

        @Override
        public T get() {
            try {
//                System.out.println(name + " is working!");
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
//            System.out.println(name + " end working!");
            return result;
        }
    }

    @Test
    public void test1Worker1Get() throws LightExecutionException, InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = pool.submit(new UselessTask<>("1", 10, 300));
        Assert.assertEquals(10, (int) future.get());
    }

    @Test
    public void testNWorkersMGets() throws LightExecutionException, InterruptedException {
        int[] ns = new int[]{1, 3, 10, 100};
        int[] ms = new int[]{0, 1, 10};

        for (int n : ns) {
            for (int m : ms) {
                ThreadPool pool = new ThreadPoolImpl(n);
                int[] numbers = new Random().ints(m, 0, 1000).toArray();
                ArrayList<LightFuture<Integer>> futures = new ArrayList<>();
                for (int i = 0; i < m; ++i) {
                    futures.add(pool.submit(new UselessTask<>(Integer.toString(i), numbers[i],
                            new Random().nextInt(250))));
                }
                for (int i = 0; i < m; ++i) {
                    Assert.assertEquals(numbers[i], (int) futures.get(i).get());
                }
            }
        }
    }

    @Test
    public void testExceptionNotThrown() {
        ThreadPool pool = new ThreadPoolImpl(10);
        for (int i = 0; i < 5; ++i) {
            pool.submit(() -> {
                throw new RuntimeException("So bad.");
            });
        }
    }

    @Test
    public void testLightExecutionException() throws InterruptedException {
        RuntimeException exception = new RuntimeException("ugh");
        ThreadPool pool = new ThreadPoolImpl(10);
        for (int i = 0; i < 5; ++i) {
            LightFuture<Object> future = pool.submit(() -> {
                throw exception;
            });
            try {
                future.get();
            } catch (LightExecutionException e) {
                Assert.assertEquals(e.getThrowable(), exception);
                continue;
            }
            Assert.assertTrue(false);
        }
    }


    @Test
    public void shutdownTest1() throws InterruptedException, LightExecutionException {
        ThreadPool pool = new ThreadPoolImpl(10);
        ArrayList<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            futures.add(pool.submit(new UselessTask<>(Integer.toString(i), i, 100000)));
        }
        pool.shutdown();
        for (int i = 0; i < futures.size(); ++i) {
            Assert.assertEquals(i, (int) futures.get(i).get());
        }
    }


    @Test
    public void shutdownTest2() throws InterruptedException {
        class LoopTask implements Supplier<Integer> {
            @Override
            public Integer get() {
                int res = 0;
                for (long i = 0; i < 10000000000L; ++i) {
                    res += i;
                }
                return res;
            }
        }

        ThreadPool pool = new ThreadPoolImpl(10);
        ArrayList<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            futures.add(pool.submit(new LoopTask()));
        }
        pool.shutdown();
    }


    /**
     * Tests if at least all workers are used...
     */
    @Test
    public void testAllWorkersDoWork() throws LightExecutionException, InterruptedException {
        final HashSet<Long> visitors = new HashSet<>();
        Supplier<Void> curiousSupplier = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            synchronized (visitors) {
                visitors.add(Thread.currentThread().getId());
            }
            return null;
        };

        int n = 5;
        int m = 15;
        ThreadPool pool = new ThreadPoolImpl(n);
        ArrayList<LightFuture> futures = new ArrayList<>();
        for (int i = 0; i < m; ++i) {
            futures.add(pool.submit(curiousSupplier));
        }
        for (LightFuture f : futures) {
            f.get();
        }

        Assert.assertEquals(n, visitors.size());
    }

    @Test
    public void testThenApplySimple() throws LightExecutionException, InterruptedException {
        int n = 5;
        int m = 15;
        ThreadPool pool = new ThreadPoolImpl(n);
        int[] numbers = new Random().ints(m, 1, 1000).toArray();
        ArrayList<LightFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < m; ++i) {
            futures.add(pool.submit(new UselessTask<>(Integer.toString(i), numbers[i], 200))
                    .thenApply(Object::toString));
        }
        for (int i = 0; i < m; ++i) {
            Assert.assertEquals(Integer.toString(numbers[i]), futures.get(i).get());
        }
    }

    @Test
    public void testDeepThenApply() throws LightExecutionException, InterruptedException {
        int n = 5;
        int m = 15;
        ThreadPool pool = new ThreadPoolImpl(n);
        int[] numbers = new Random().ints(m, 1, 1000).toArray();
        ArrayList<LightFuture> futures = new ArrayList<>();
        for (int i = 0; i < m; ++i) {
            futures.add(pool.submit(new UselessTask<>(Integer.toString(i), numbers[i], 500))
                    .thenApply(Function.identity())
                    .thenApply(Function.identity())
                    .thenApply(Function.identity()));
        }
        for (int i = 0; i < m; ++i) {
            Assert.assertEquals(numbers[i], (int) futures.get(i).get());
        }
    }

    @Test
    public void testTimeCostThenApply() throws LightExecutionException, InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(5);
        LightFuture<Integer> f = pool.submit(new UselessTask<>("1", 10, 500)).thenApply(
                x -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 2 * x;
                }
        );
        Assert.assertEquals(20, (int) f.get());
    }

    @Test(expected = LightExecutionException.class)
    public void testThenApplyException() throws LightExecutionException, InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(5);
        LightFuture<Integer> f = pool.submit(new UselessTask<>("1", 10, 500)).thenApply(
                x -> { throw new IllegalStateException("=)"); }
        );
        f.get();
    }
}
