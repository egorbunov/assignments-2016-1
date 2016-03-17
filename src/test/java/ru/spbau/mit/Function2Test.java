package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by Egor Gorbunov on 21.03.2016.
 * email: egor-mailbox@ya.ru
 */
public class Function2Test {
    private static final Function1<Object, String> TO_STR = new Function1<Object, String>() {
        @Override
        public String apply(Object o) {
            return o.toString();
        }
    };

    private static final Function2<Integer, Integer, Integer> MY_SUBTRACT
            = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer a, Integer b) {
            return a - b;
        }
    };

    private static Random random = new Random();

    private int a;
    private int b;

    @Before
    public void setup() {
        a = random.nextInt();
        b = random.nextInt();
    }


    @Test
    public void testApply() {
        Assert.assertEquals(a - b, (int) MY_SUBTRACT.apply(a, b));
    }

    @Test
    public void testBind1() {
        Function1<Integer, Integer> subB = MY_SUBTRACT.bind2(b);
        Assert.assertEquals(a - b, (int) subB.apply(a));
    }

    @Test
    public void testBind2() {
        Function1<Integer, Integer> subFromA = MY_SUBTRACT.bind1(a);
        Assert.assertEquals(a - b, (int) subFromA.apply(b));
    }

    @Test
    public void testCurry() {
        Function1<Integer, Function1<Integer, Integer>> curry = MY_SUBTRACT.curry();
        Assert.assertEquals(a - b, (int) curry.apply(a).apply(b));
    }

    @Test
    public void testCompose() {
        Assert.assertEquals(MY_SUBTRACT.compose(TO_STR).apply(a, b), Integer.toString(a - b));
    }
}
