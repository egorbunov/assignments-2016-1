package ru.spbau.mit;

import java.util.ArrayList;
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
     * Future, which scheduled by ThreadPoolImpl
     * @param <R> Future result type
     */
    private class Future<R> implements LightFuture<R> {
        private final Supplier<R> work;
        private volatile boolean ready = false;
        private R result = null;
        private Throwable executionException = null;
        private ArrayList<Future> dependentTasks = new ArrayList<>();

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
            flushDependentTasksToQueue();
            notifyAll();
        }

        private void flushDependentTasksToQueue() {
            synchronized (ThreadPoolImpl.this.taskQueue) {
                ThreadPoolImpl.this.taskQueue.addAll(dependentTasks);
                dependentTasks.clear();
            }
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public R get() throws LightExecutionException, InterruptedException {
            synchronized (this) {
                while (!ready) {
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
            Future<U> dependentTask = new Future<>(() -> {
                if (!ready) {
                    throw new IllegalStateException("Result already must be ready!");
                }
                return f.apply(result);
            });
            dependentTasks.add(dependentTask);

            if (ready) {
                flushDependentTasksToQueue();
            }

            return dependentTask;
        }
    }

    private <R> void addFutureTask(Future<R> future) {
        synchronized (taskQueue) {
            taskQueue.addLast(future);
            taskQueue.notifyAll();
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        Future<R> future = new Future<>(supplier);
        addFutureTask(future);
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
