package com.github.danirod12.luckyblock.api.exception;

public class PremiumVersionRequiredException extends RuntimeException {

    public PremiumVersionRequiredException() {
        this("Requested feature");
    }

    public PremiumVersionRequiredException(String feature) {
        super(feature + " is not available in this version type. You need a premium version");
    }
}
