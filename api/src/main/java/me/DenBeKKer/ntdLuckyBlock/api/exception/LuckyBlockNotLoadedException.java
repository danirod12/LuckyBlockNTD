package me.DenBeKKer.ntdLuckyBlock.api.exception;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;

public class LuckyBlockNotLoadedException extends Exception {

    public LuckyBlockNotLoadedException(LuckyBlockKey type) {
        super(type.getKey() + " not loaded, you cant place it");
    }
}
