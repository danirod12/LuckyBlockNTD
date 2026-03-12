package com.github.danirod12.luckyblock.api.loader;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;

public interface StringLoader {

    @Deprecated
    default LuckyDrop load(String string) {
        try {
            return this.deserialize(string);
        } catch (Exception exception) {
            return null;
        }
    }

    LuckyDrop deserialize(String string) throws Exception;

    String serialize(LuckyDrop drop);
}
