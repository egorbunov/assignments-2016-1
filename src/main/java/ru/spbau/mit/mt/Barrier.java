package ru.spbau.mit.mt;

public class Barrier {
    private final int parties;
    private int cur_num = 0;

    public Barrier(int parties) {
        this.parties = parties;
    }

    public synchronized void await() throws InterruptedException {
        cur_num += 1;
        while (cur_num < parties) {
            wait();
        }
        notifyAll();
    }
}
