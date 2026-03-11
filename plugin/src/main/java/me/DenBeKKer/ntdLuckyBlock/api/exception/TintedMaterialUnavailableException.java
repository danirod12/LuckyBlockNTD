package me.DenBeKKer.ntdLuckyBlock.api.exception;

public class TintedMaterialUnavailableException extends RuntimeException {

    public TintedMaterialUnavailableException() {
        super("Your platform not support Tinted glass (lower than 1.17) or your version not a premium one");
    }
}
