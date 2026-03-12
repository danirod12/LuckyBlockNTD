package com.github.danirod12.luckyblock.api.loader;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;

public interface CustomSaver {

    static LuckyDrop load(String description) {
        throw new UnsupportedOperationException("Method not initialized");
    }

    String getDescription();
}
