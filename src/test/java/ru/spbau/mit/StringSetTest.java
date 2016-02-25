package ru.spbau.mit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class StringSetTest {
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static Random rnd = new Random();

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private static Set<String> stringsDict;
    private static Set<String> stringDictSubset; // subset of stringDict
    private static Set<String> differentStringDict; // intersection with stringDict is empty
    private StringSet strSet;

    @Before
    public void beforeTest() {
        strSet = new StringSetImpl();
    }

    @BeforeClass
    static public void prepare() {
        final int N = 10000;
        final int SUB_N = N / 10;
        final int N1 = 1000;
        final int MAX_LEN = 100;
        final int MIN_LEN = 1;

        stringsDict = new HashSet<>(N);
        stringDictSubset = new HashSet<>(SUB_N);
        int curSubsetSize = 0;
        for (int i = 0; i < N; i++) {
            String str = randomString(rnd.nextInt(MAX_LEN - MIN_LEN + 1) + MIN_LEN);
            stringsDict.add(str);
            if (curSubsetSize++ < SUB_N) {
                stringDictSubset.add(str);
            }
        }

        differentStringDict = new HashSet<>(N1);
        for (int i = 0; i < N1; ) {
            String str = randomString(rnd.nextInt(MAX_LEN - MIN_LEN + 1) + MIN_LEN);
            if (!stringsDict.contains(str)) {
                i++;
                differentStringDict.add(str);
            }
        }
    }

    @Test
    public void testAdd() {
        for (String s : stringsDict) {
            assertTrue(strSet.add(s));
        }
        assertEquals(strSet.size(), stringsDict.size());
        for (String s : stringsDict) {
            assertTrue(strSet.contains(s));
            assertFalse(strSet.add(s));
        }
    }

    @Test
    public void testContains() {
        for (String s : stringsDict) {
            strSet.add(s);
        }
        for (String s : stringDictSubset) {
            assertTrue(strSet.contains(s));
        }
        for (String s : differentStringDict) {
            assertFalse(strSet.contains(s));
        }
    }

    @Test
    public void testRemove() {
        for (String s : stringsDict) {
            strSet.add(s);
        }
        for (String s : stringDictSubset) {
            assertTrue(strSet.remove(s));
        }
        assertEquals(strSet.size(), stringsDict.size() - stringDictSubset.size());
        for (String s : differentStringDict) {
            assertFalse(strSet.remove(s));
        }
    }

    @Test
    public void testWordsNumWithPrefix() {
        final int TN = 100;
        for (int i = 0; i < TN; i++) {
            strSet = new StringSetImpl();
            int expected = 0;
            String prefix = randomString(rnd.nextInt(2) + 1);
            for (String s : stringsDict) {
                if (s.startsWith(prefix)) {
                    expected++;
                }
                strSet.add(s);
            }
            assertEquals(expected, strSet.howManyStartsWithPrefix(prefix));
        }
    }

    @Test
    public void testSpecialCases() {
        assertEquals(strSet.size(), 0);
        assertFalse(strSet.add(""));
        assertFalse(strSet.remove(""));
        assertFalse(strSet.remove("dsdsd"));
    }

    @Test(expected = Exception.class)
    public void testBadStringAdded() {
        strSet.add("21-392-03.,.231293-12039");
    }


    @Test
    public void testSimple() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(1, stringSet.size());
        assertEquals(1, stringSet.howManyStartsWithPrefix("abc"));
    }

    public static StringSet instance() {
        try {
            return (StringSet) Class.forName("ru.spbau.mit.StringSetImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Error while class loading");
    }
}
