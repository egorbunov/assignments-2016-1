package ru.spbau.mit.generics;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Egor Gorbunov on 15.03.16.
 * email: egor-mailbox@ya.ru
 */
public class Notes {
    /**
     * 1) U can't push anything in collection x
     * {@code <?>} means that there is some restriction on type in collection {@code x}, but it is not known
     * 2) U can push everything in collection y
     */
    public static void test(Collection<?> x, Collection<Object> y) {
        y.addAll(x);
    }

    public static <T> void myAddAll(Collection<? extends T> x, Collection<T> y) {
        y.addAll(x);
    }


    /**
     * That is very similar to {@code Collection<?>}
     */
    public static <T> void test1(Collection<T> xs) {
        ((Collection<String>) xs).add("Hello.");
    }

    public static void test2(Collection<?> xs) {
        ((Collection<String>) xs).add("Hello.");
    }

    /**
     * Class cast exception
     */
    public static void classCastExceptionFail(Collection<?> xs) {
        ArrayList<Integer> integers = new ArrayList<>();

        Notes.<Integer>test1(integers);

        test2(integers);

        Integer x = integers.get(0);
    }

    public static void main(String[] args) {
        ArrayList<Integer> integers = new ArrayList<>();
        ArrayList<Integer> ys = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};

        myAddAll(ys, integers);
    }
}
