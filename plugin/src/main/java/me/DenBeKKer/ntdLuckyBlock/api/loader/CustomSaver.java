package me.DenBeKKer.ntdLuckyBlock.api.loader;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;

public interface CustomSaver {

    static LuckyDrop load(String description) {
        throw new UnsupportedOperationException("Method not initialized");
    }

    String getDescription();
}
