package com.github.danirod12.luckyblock.api.exception;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;

public class LuckyBlockNotLoadedException extends Exception {

    public LuckyBlockNotLoadedException(LuckyBlockKey type) {
        super(type.getKey() + " not loaded, you cant place it");
    }
}
