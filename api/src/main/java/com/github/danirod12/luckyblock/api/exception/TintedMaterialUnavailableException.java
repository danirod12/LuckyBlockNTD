package com.github.danirod12.luckyblock.api.exception;

public class TintedMaterialUnavailableException extends RuntimeException {

    public TintedMaterialUnavailableException() {
        super("Your platform not support Tinted glass (lower than 1.17) or your version not a premium one");
    }
}
