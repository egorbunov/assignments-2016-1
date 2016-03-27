package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by Egor Gorbunov on 15.03.16.
 * email: egor-mailbox@ya.ru
 */

public class Function1Test {
    private static final Function1<Object, String> TO_STR = new Function1<Object, String>() {
        @Override
        public String apply(Object o) {
            return o.toString();
        }
    };

    private static final Function1<String, Integer> PARSE_INT = new Function1<String, Integer>() {
        @Override
        public Integer apply(String s) {
            return Integer.valueOf(s);
        }
    };

    private static Random random = new Random();

    @Test
    public void testApply() {
        int x = random.nextInt();
        Assert.assertEquals(Integer.toString(x), TO_STR.apply(x));
    }

    @Test
    public void testCompose() {
        Object obj = random.nextInt();
        Assert.assertEquals(obj, TO_STR.compose(PARSE_INT).apply(obj));
    }

    @Test
    public void testSignatureIsFlexible() {
        String str = String.valueOf(random.nextInt());
        Assert.assertEquals(str, PARSE_INT.compose(TO_STR).apply(str));
    }
}
