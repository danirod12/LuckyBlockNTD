package com.github.danirod12.luckyblock.api.model;

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
    BLACK_HOLE(0),
    GHOST_MODE(0),
    MOON_GRAVITY(0),
    RANDOM_TELEPORT(0),
    SLIPPERY_FINGERS(0),
    TIME_LOOP(0);

    private final int defaultValue;

    SpecialDropType(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int defaultValue() {
        return defaultValue;
    }
}
