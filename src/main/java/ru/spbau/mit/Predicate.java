package ru.spbau.mit;

/**
 * Created by Egor Gorbunov on 21.03.2016.
 * email: egor-mailbox@ya.ru
 */
public abstract class Predicate<T> extends Function1<T, Boolean> {
    public static final Predicate<Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return true;
        }
    };

    public static final Predicate<Object> ALWAYS_FALSE = ALWAYS_TRUE.not();

    public Predicate<T> or(final Predicate<? super T> other) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return Predicate.this.apply(t) || other.apply(t);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> other) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return Predicate.this.apply(t) && other.apply(t);
            }
        };
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return !Predicate.this.apply(t);
            }
        };
    }
}
