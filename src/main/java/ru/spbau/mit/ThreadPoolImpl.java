package ru.spbau.mit;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private volatile boolean isWorking = true;
    private final LinkedList<Future> taskQueue = new LinkedList<>();
    private Thread[] workers;

    public ThreadPoolImpl(int n) {
        workers = new Thread[n];
        for (int i = 0; i < n; ++i) {
            workers[i] = new Thread(new RunnableWorker(i));
            workers[i].start();
        }
    }

    private void recreateWorker(int id) {
        workers[id] = new Thread(new RunnableWorker(id));
        workers[id].start();
    }

    private class RunnableWorker implements Runnable {
        private final int id;

        RunnableWorker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (isWorking) {
                Future future;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            if (isWorking) {
                                recreateWorker(id);
                            }
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    future = taskQueue.removeFirst();
                }
                // Working...
                future.doWork();
            }
        }
    }

    /**
     * Future, which depends on some another future and which result is
     * just transformed result of that dependency future evaluation
     * @param <R> Future result type
     * @param <T> Dependent future result type
     */
    private class DependentFuture<R, T> implements LightFuture<R> {
        private final Function<? super T, ? extends R> transformer;
        private final LightFuture<T> dependency;

        DependentFuture(Function<? super T, ? extends R> transformer,
                        LightFuture<T> dependency) {
            this.transformer = transformer;
            this.dependency = dependency;
        }

        @Override
        public boolean isReady() {
            return dependency.isReady();
        }

        @Override
        public R get() throws LightExecutionException, InterruptedException {
            return transformer.apply(dependency.get());
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            return new DependentFuture<>(f, this);
        }
    }

    /**
     * Future, which scheduled by ThreadPoolImpl
     * @param <R> Future result type
     */
    private class Future<R> implements LightFuture<R> {
        private final Supplier<R> work;
        private volatile boolean ready = false;
        private R result = null;
        private Throwable executionException = null;

        Future(Supplier<R> supplier) {
            work = supplier;
        }

        void doWork() {
            try {
                result = work.get();
            } catch (Exception e) {
                executionException = e;
            }
            setReady();
        }

        private synchronized void setReady() {
            ready = true;
            notifyAll();
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public R get() throws LightExecutionException, InterruptedException {
            synchronized (this) {
                while (!isReady()) {
                    wait();
                }
            }

            if (executionException != null) {
                throw new LightExecutionException(executionException);
            }

            return result;
        }

        @Override
        public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
            return new DependentFuture<>(f, this);
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        Future<R> future = new Future<>(supplier);
        synchronized (taskQueue) {
            taskQueue.addLast(future);
            taskQueue.notifyAll();
        }
        return future;
    }

    @Override
    public void shutdown() {
        isWorking = false;
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}
