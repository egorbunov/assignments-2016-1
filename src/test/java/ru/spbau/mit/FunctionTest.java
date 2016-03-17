package ru.spbau.mit;

import org.junit.Test;

/**
 * Created by Egor Gorbunov on 15.03.16.
 * email: egor-mailbox@ya.ru
 */

public class FunctionTest {

    class A {

    }

    class B extends A {

    }

    @Test
    void testComposeSignature() {
        Function1<Integer, Integer> mul = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer * 2;
            }
        };

        Function1<Object, String> toString = new Function1<Object, String>() {
            @Override
            public String apply(Object o) {
                return o.toString();
            }
        };

        mul.compose(toString);
    }
}
