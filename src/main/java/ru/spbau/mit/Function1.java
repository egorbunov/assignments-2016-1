package ru.spbau.mit;

/**
 * Created by Egor Gorbunov on 15.03.16.
 * email: egor-mailbox@ya.ru
 */
public abstract class Function1<T, R> {
    public abstract R apply(T t);

    /**
     * Trick with signature {@code <? super R, Rg>} needed because
     * it's okay then {@code g} takes argument of class X, which
     * R can be casted to.
     *
     * @param g - function to compose this with
     */
    public <Rg> Function1<T, Rg> compose(final Function1<? super R, Rg> g) {
        return new Function1<T, Rg>() {

            @Override
            public Rg apply(T t) {
                return g.apply(Function1.this.apply(t));
            }
        };
    }
}
