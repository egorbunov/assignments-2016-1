package ru.spbau.mit;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Egor Gorbunov on 16.02.16.
 * email: egor-mailbox@ya.ru
 */
public class StringSetImpl implements StringSet {
    private Set<String> stringHashSet = new HashSet<>();

    @Override
    public boolean add(String element) {
        return stringHashSet.add(element);
    }

    @Override
    public boolean contains(String element) {
        return stringHashSet.contains(element);
    }

    @Override
    public boolean remove(String element) {
        return stringHashSet.remove(element);
    }

    @Override
    public int size() {
        return stringHashSet.size();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        int cnt = 0;
        for (String x : stringHashSet) {
            if (x.startsWith(prefix)) {
                cnt += 1;
            }
        }
        return cnt;
    }
}
