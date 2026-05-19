package com.github.danirod12.luckyblock.engine.loader;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;

public class SpecialDropLoader implements LuckyDrop {
    @Override
    public void execute(Execution execution) {
        throw new UnsupportedOperationException("This drop is only used for loading " +
                "special drops, it cannot be executed");
    }

    @Deprecated
    public static LuckyDrop deserialize(String[] data) {
        // TODO rework
        return new LegacyLoader(null, null).deserialize("SPECIAL : "
                + String.join(" : ", data));
    }

    @Deprecated
    public static String[] serialize(LuckyDrop drop) {
        // TODO rework
        return new LegacyLoader(null, null).serialize(drop)
                .substring("SPECIAL : ".length()).split(" : ");
    }
}
