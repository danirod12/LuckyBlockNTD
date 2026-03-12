package com.github.danirod12.luckyblock.api.exception;

public class ApiNotInitializedException extends RuntimeException {

    public ApiNotInitializedException() {
        super("API not initialized yet. Add ntdLuckyBlock as a dependency");
    }
}
