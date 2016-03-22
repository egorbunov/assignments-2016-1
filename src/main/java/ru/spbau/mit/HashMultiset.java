package ru.spbau.mit;

import java.util.*;

/**
 * Created by Egor Gorbunov on 22.03.16.
 * email: egor-mailbox@ya.ru
 */
public class HashMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
    private LinkedHashMap<E, Integer> map = new LinkedHashMap<>();
    private int size = 0;

    private class HashMultisetEntry<E> implements Entry<E> {
        private final E e;
        private final int cnt;

        HashMultisetEntry(E e, int cnt) {
            this.e = e;
            this.cnt = cnt;
        }

        @Override
        public E getElement() {
            return e;
        }

        @Override
        public int getCount() {
            return cnt;
        }
    }

    private class EntrySet<T> extends AbstractSet<T> {

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private Iterator<Map.Entry<E, Integer>> it = HashMultiset.this.map.entrySet().iterator();
                private HashMultisetEntry<E> prevEntry = null;

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public void remove() {
                    HashMultiset.this.size -= prevEntry.cnt;
                    it.remove();
                }

                @Override
                public T next() {
                    Map.Entry<E, Integer> next = it.next();
                    prevEntry = new HashMultisetEntry<>(next.getKey(), next.getValue());
                    return (T) prevEntry;
                }
            };
        }

        @Override
        public int size() {
            return HashMultiset.this.map.size();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object x : c) {
            if (!contains(x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean res = false;
        for (Object x : c) {
            res |= remove(x);
        }
        return res;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o)) {
            return false;
        }
        int cnt = map.get(o);
        if (cnt == 1) {
            map.remove(o);
        } else {
            map.put((E) o, cnt - 1);
        }
        size -= 1;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return count(o) > 0;
    }

    @Override
    public int count(Object element) {
        Integer res = map.get(element);
        if (res == null) {
            return 0;
        } else {
            return res;
        }
    }

    @Override
    public Set<E> elementSet() {
        return map.keySet();
    }

    @Override
    public Set<? extends Entry<E>> entrySet() {
        return new EntrySet<>();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Iterator<Map.Entry<E, Integer>> entryIt = map.entrySet().iterator();
            private Map.Entry<E, Integer> curEnry = null;
            private int curBinSize = 0;
            private int prevBinPos = -1;

            @Override
            public boolean hasNext() {
                return entryIt.hasNext() || prevBinPos + 1 < curBinSize;
            }

            @Override
            public E next() {
                prevBinPos += 1;
                if (curEnry == null || prevBinPos >= curBinSize) {
                    curEnry = entryIt.next();
                    curBinSize = curEnry.getValue();
                    prevBinPos = 0;
                }
                return curEnry.getKey();
            }

            @Override
            public void remove() {
                if (curEnry.getValue() == 1) {
                    entryIt.remove();
                    curBinSize = 0;
                    prevBinPos = -1;
                    curEnry = null;
                } else {
                    curEnry.setValue(curEnry.getValue() - 1);
                }
                size -= 1;
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E e) {
        if (!map.containsKey(e)) {
            map.put(e, 0);
        }
        map.put(e, map.get(e) + 1);
        size++;
        return true;
    }
}
