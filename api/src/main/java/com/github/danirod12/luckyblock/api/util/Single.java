package com.github.danirod12.luckyblock.api.util;

import java.util.Optional;

/**
 * A simple container that can hold a single value of type T.
 *
 * @param <T> the type of the value to be held
 */
public class Single<T> {

    private T t;

    public Single() {
        this.t = null;
    }

    public Single(T t) {
        this.t = t;
    }

    public T get() {
        return this.t;
    }

    public T set(T t) {
        return this.t = t;
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(this.t);
    }
}
