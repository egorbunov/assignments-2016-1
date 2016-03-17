package ru.spbau.mit;

/**
 * Created by Egor Gorbunov on 21.03.2016.
 * email: egor-mailbox@ya.ru
 */
public abstract class Function2<T1, T2, R> {
    public abstract R apply(T1 a, T2 b);

    public <Rg> Function2<T1, T2, Rg> compose(final Function1<? super R, Rg> g) {
        return new Function2<T1, T2, Rg>() {
            @Override
            public Rg apply(T1 a, T2 b) {
                return g.apply(Function2.this.apply(a, b));
            }
        };
    }

    public Function1<T2, R> bind1(T1 a) {
        return curry().apply(a);
    }

    public Function1<T1, R> bind2(T2 b) {
        final T2 capture = b;
        return new Function1<T1, R>() {
            @Override
            public R apply(T1 a) {
                return Function2.this.apply(a, capture);
            }
        };
    }

    public Function1<T1, Function1<T2, R>> curry() {
        return new Function1<T1, Function1<T2, R>>() {
            @Override
            public Function1<T2, R> apply(T1 a) {
                final T1 capture = a;
                return new Function1<T2, R>() {
                    @Override
                    public R apply(T2 b) {
                        return Function2.this.apply(capture, b);
                    }
                };
            }
        };
    }
}
