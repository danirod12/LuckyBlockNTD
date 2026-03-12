package com.github.danirod12.luckyblock.api.exception;

public class StaticMethodsOnlyException extends UnsupportedOperationException {

    public StaticMethodsOnlyException() {
        super("A class with static methods only");
    }
}
