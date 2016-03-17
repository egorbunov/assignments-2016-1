package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Created by Egor Gorbunov on 21.03.2016.
 * email: egor-mailbox@ya.ru
 */
public class PredicateTest {
    private static final Predicate<Integer> IS_EVEN = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer integer) {
            return Math.abs(integer) % 2 == 0;
        }
    };

    private static final Predicate<Integer> IS_ODD = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer integer) {
            return Math.abs(integer) % 2 == 1;
        }
    };

    private int[] numbers;

    @Before
    public void prepare() {
        final int n = 1000;
        Random random = new Random();
        numbers = new int[n];
        for (int i = 0; i < numbers.length; ++i) {
            numbers[i] = random.nextInt();
        }
    }

    @Test
    public void testAlwaysPredicates() {
        Collection<Object> objects = new ArrayList<>();
        objects.add("Hello, world!");
        objects.add(new ArrayList<String>());
        objects.add(IS_EVEN);
        objects.add(IS_ODD);
        objects.add(objects.hashCode());
        objects.add(true);
        objects.add(false);

        for (Object o : objects) {
            Assert.assertFalse(Predicate.ALWAYS_FALSE.apply(o));
            Assert.assertTrue(Predicate.ALWAYS_TRUE.apply(o));
        }
    }

    @Test
    public void testOr() {
        Predicate<Integer> anyNum = IS_EVEN.or(IS_ODD);
        Predicate<Object> any = Predicate.ALWAYS_FALSE.or(Predicate.ALWAYS_TRUE);

        for (int n : numbers) {
            Assert.assertTrue(anyNum.apply(n));
            Assert.assertTrue(any.apply(n));
        }
    }

    @Test
    public void testAnd() {
        Predicate<Integer> noOneNum = IS_EVEN.and(IS_ODD);
        Predicate<Object> noOne = Predicate.ALWAYS_FALSE.and(Predicate.ALWAYS_TRUE);

        for (int n : numbers) {
            Assert.assertFalse(noOneNum.apply(n));
            Assert.assertFalse(noOne.apply(n));
        }
    }

    @Test
    public void testNot() {
        Predicate<Integer> notEven = IS_EVEN.not();
        Predicate<Integer> notOdd = IS_ODD.not();
        Predicate<Object> truePred = Predicate.ALWAYS_FALSE.not();

        for (int n : numbers) {
            Assert.assertEquals(IS_ODD.apply(n), notEven.apply(n));
            Assert.assertEquals(IS_EVEN.apply(n), notOdd.apply(n));
            Assert.assertTrue(truePred.apply(n));
        }
    }
}
