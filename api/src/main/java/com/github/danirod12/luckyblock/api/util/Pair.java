package com.github.danirod12.luckyblock.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * A simple generic class that holds a pair of values.
 *
 * @param <A> the type of the first value
 * @param <B> the type of the second value
 */
@Getter
@Setter
@AllArgsConstructor
public class Pair<A, B> {

    private A a;
    private B b;

    public static <A, B> Pair<A, B> from(Map.Entry<A, B> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    public A getKey() {
        return a;
    }

    public void setKey(A a) {
        this.a = a;
    }

    public B getValue() {
        return b;
    }

    public void setValue(B b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + a +
                ", value=" + b +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Pair) {
            Pair<?, ?> pair = (Pair<?, ?>) object;
            return pair.getKey().equals(getKey()) && pair.getValue().equals(getValue());
        }
        return false;
    }
}
