package ru.spbau.mit.mt;


public class BarrierTest {
    public static void main(String[] args) {
        final int cnt = 3;
        final Barrier b = new Barrier(cnt);

        class MyRunnable implements Runnable {

            private final int timeToWork;
            private final String name;

            MyRunnable(int timeToWork, String name) {
                this.timeToWork = timeToWork;
                this.name = name;
            }

            @Override
            public void run() {
                try {
                    System.out.println(name + " is working...");
                    Thread.sleep(timeToWork);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Now let's wait for others...");
                try {
                    b.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Now I'am running...");
            }
        }


        new Thread(new MyRunnable(1000, "Worker 1")).start();
        new Thread(new MyRunnable(3000, "Worker 2")).start();
        new Thread(new MyRunnable(5000, "Worker 3")).start();
    }
}
