package com.github.danirod12.luckyblock.api.model;

/**
 * Enum representing different types of special drops in the Lucky Block plugin.
 * Each type has a default value associated with it, which can be used for configuration or reference.
 * <p>
 * For more info visit the wiki:
 * <a href="https://danirod12.github.io/ntd-wiki/docs/luckyblock/setup/color#pigspecial-pig">drops setup page</a>
 */
public enum SpecialDropType {
    PIG(4),
    LIGHTNING(3),
    WATER_BUCKET(64),
    DIAMOND_COLUMN(-1),
    TNT_COLUMN(5),
    TNT_EXPLOSION(20),
    EXPERIENCE_EXPLOSION(45),
    JEB(5),
    CREEPY_MUSIC(0),
    CHICKEN_RAIN(15),
    PARANOIA(0),
    ANNOYING_BABY(3),
    HOTBAR_SWAP(0),

    ;

    private final int defaultValue;

    SpecialDropType(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int defaultValue() {
        return defaultValue;
    }
}
