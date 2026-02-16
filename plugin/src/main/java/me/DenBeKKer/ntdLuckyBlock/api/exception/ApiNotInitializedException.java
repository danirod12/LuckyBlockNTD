package me.DenBeKKer.ntdLuckyBlock.api.exception;

public class ApiNotInitializedException extends RuntimeException {

    public ApiNotInitializedException() {
        super("API not initialized yet. Add ntdLuckyBlock as a dependency");
    }
}
