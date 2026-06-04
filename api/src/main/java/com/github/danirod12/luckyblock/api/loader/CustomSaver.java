package com.github.danirod12.luckyblock.api.loader;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;

/**
 * Deprecated class for V2p -> V3 conversion
 */
@Deprecated
public interface CustomSaver {

    static LuckyDrop load(String description) {
        throw new UnsupportedOperationException("Method not initialized");
    }

    String getDescription();
}
