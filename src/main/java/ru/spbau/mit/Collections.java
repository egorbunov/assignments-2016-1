package ru.spbau.mit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Egor Gorbunov on 21.03.2016.
 * email: egor-mailbox@ya.ru
 */
public final class Collections {

    public static <T, R> Collection<R> map(Function1<? super T, R> fun, Collection<T> collection) {
        ArrayList<R> mapped = new ArrayList<>();
        for (T a : collection) {
            mapped.add(fun.apply(a));
        }
        return mapped;
    }

    public static <T> Collection<T> filter(Predicate<? super T> p, Collection<T> collection) {
        ArrayList<T> filtered = new ArrayList<>();
        for (T a : collection) {
            if (p.apply(a)) {
                filtered.add(a);
            }
        }
        return filtered;
    }

    public static <T> Collection<T> takeWhile(Predicate<? super T> p, Collection<T> collection) {
        ArrayList<T> res = new ArrayList<>();
        for (T a : collection) {
            if (!p.apply(a)) {
                break;
            }
            res.add(a);
        }
        return res;
    }

    public static <T> Collection<T> takeUnless(Predicate<? super T> p, Collection<T> collection) {
        return takeWhile(p.not(), collection);
    }

    /**
     *  f....(f(f(f(init, a[0]), a[1]).....))))))
     */
    public static <T, R> R foldl(Function2<? super R, ? super T, R> fun, R init, Collection<T> collection) {
        R res = init;
        for (T a : collection) {
            res = fun.apply(res, a);
        }
        return res;
    }

    /**
     * f(...f(a[n-2], f(a[n-1], f(a[n], init)))...)
    */
    public static <T, R> R foldr(Function2<? super T, ? super R, R> fun, R init, Collection<T> collection) {
        return recursiveFoldr(fun, init, collection.iterator());
    }

    private static <T, R> R recursiveFoldr(Function2<? super T, ? super R, R> fun, R init, Iterator<T> it) {
        if (!it.hasNext()) {
            return init;
        }
        return fun.apply(it.next(), recursiveFoldr(fun, init, it));
    }

    /**
     * Utility classes should't be constructable
     */
    private Collections() {
    }
}
