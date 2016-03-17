package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Egor Gorbunov on 21.03.2016.
 * email: egor-mailbox@ya.ru
 */
public class CollectionsTest {
    private static final Function2<Integer, Integer, Integer> MY_SUBTRACT
            = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer a, Integer b) {
            return a - b;
        }
    };

    private static final Function1<Object, String> TO_STR = new Function1<Object, String>() {
        @Override
        public String apply(Object o) {
            return o.toString();
        }
    };

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


    private static Random random = new Random();
    private Set<String> numStrings;
    private Set<Integer> numbers;
    private Set<Integer> evenPart;

    @Before
    public void prepare() {
        final int max = 10000;
        final int n = 5000;
        numStrings = new HashSet<>();
        numbers = new HashSet<>();
        evenPart = new HashSet<>();
        for (int i = 0; i < n; ++i) {
            int x = random.nextInt(2 * max) - max;
            numbers.add(x);
            numStrings.add(Integer.toString(x));
            if (IS_EVEN.apply(x)) {
                evenPart.add(x);
            }
        }
    }

    @Test
    public void testMap() {
        Collection<String> mapped = Collections.map(TO_STR, numbers);
        Assert.assertEquals(numStrings.size(), mapped.size());
        Assert.assertTrue(numStrings.containsAll(mapped));
    }

    @Test
    public void testFilter() {
        Collection<Integer> onlyEven = Collections.filter(IS_EVEN, numbers);
        Assert.assertEquals(onlyEven.size(), evenPart.size());
        Assert.assertTrue(evenPart.containsAll(onlyEven));

        Assert.assertEquals(Collections.filter(Predicate.ALWAYS_FALSE, numbers).size(), 0);
        Assert.assertEquals(Collections.filter(Predicate.ALWAYS_TRUE, numbers).size(), numbers.size());
    }

    @Test
    public void testTakeWhileAndUnless() {
        Integer[] numArr = numbers.toArray(new Integer[numbers.size()]);
        final List<Integer> numList = Arrays.asList(numArr);
        final int idx = random.nextInt(numList.size());

        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x.equals(numList.get(idx));
            }
        };

        Collection<Integer> beforeIdx1 = Collections.takeWhile(pred.not(), numList);
        Collection<Integer> beforeIdx2 = Collections.takeUnless(pred, numList);

        Assert.assertEquals(beforeIdx1.size(), idx);
        Assert.assertEquals(beforeIdx2.size(), idx);
        Iterator<Integer> it1 = beforeIdx1.iterator();
        Iterator<Integer> it2 = beforeIdx2.iterator();
        for (int i = 0; i < idx; ++i) {
            Assert.assertEquals(numList.get(i), it1.next());
            Assert.assertEquals(numList.get(i), it2.next());
        }
    }

    @Test
    public void testFoldl() {
        Integer[] numArr = numbers.toArray(new Integer[numbers.size()]);
        int ans = 0;
        for (Integer x : numArr) {
            ans -= x;
        }
        Integer actual = Collections.foldl(MY_SUBTRACT, 0, Arrays.asList(numArr));
        Assert.assertEquals(ans, (int) actual);
    }

    @Test
    public void testFoldr() {
        Integer[] numArr = numbers.toArray(new Integer[numbers.size()]);
        int ans = 0;
        for (int i = numArr.length - 1; i >= 0; i--) {
            ans = numArr[i] - ans;
        }
        Integer actual = Collections.foldr(MY_SUBTRACT, 0, Arrays.asList(numArr));
        Assert.assertEquals(ans, (int) actual);
    }

    @Test
    public void testEmptyCollection() {
        List<Integer> emptyList = new ArrayList<>();

        Assert.assertEquals(Collections.map(TO_STR, emptyList).size(), 0);
        Assert.assertEquals(Collections.filter(Predicate.ALWAYS_TRUE, emptyList).size(), 0);
        Assert.assertEquals(Collections.takeWhile(Predicate.ALWAYS_TRUE, emptyList).size(), 0);
        Assert.assertEquals(Collections.takeUnless(Predicate.ALWAYS_FALSE, emptyList).size(), 0);
        Assert.assertEquals((int) Collections.foldr(MY_SUBTRACT, 0, emptyList), 0);
        Assert.assertEquals((int) Collections.foldl(MY_SUBTRACT, 0, emptyList), 0);
    }

    @Test
    public void testFoldlConcat() {
        final String init = "Hello";
        Integer[] numArr = numbers.toArray(new Integer[numbers.size()]);

        Function2<String, Integer, String> fun = new Function2<String, Integer, String>() {
            @Override
            public String apply(String a, Integer b) {
                return a + (b % 2);
            }
        };
        StringBuilder sb = new StringBuilder(init);
        for (int n : numArr) {
            sb.append(n % 2);
        }
        String actual = Collections.foldl(fun, init, Arrays.asList(numArr));
        Assert.assertEquals(sb.toString(), actual);
    }

    @Test
    public void testFoldrConcat() {
        final String init = "Hello";
        Integer[] numArr = numbers.toArray(new Integer[numbers.size()]);

        Function2<Integer, String, String> fun = new Function2<Integer, String, String>() {
            @Override
            public String apply(Integer a, String b) {
                return (a % 2) + b;
            }
        };
        String res = init;
        for (int i = numArr.length - 1; i >= 0; i--) {
            res = Integer.toString(numArr[i] % 2) + res;
        }
        String actual = Collections.foldr(fun, init, Arrays.asList(numArr));
        Assert.assertEquals(res, actual);
    }

    /**
     * Because of test coverage...
     */
    @Test
    public void testCollectionsConstructorIsPrivate() throws NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        Constructor<Collections> constructor = Collections.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
