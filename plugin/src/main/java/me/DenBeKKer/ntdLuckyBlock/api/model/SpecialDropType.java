package me.DenBeKKer.ntdLuckyBlock.api.model;

enum SpecialDropType {

    PIG(4),
    LIGHTNING(3),
    WATER_BUCKET(64),
    DIAMOND_COLUMN(-1),
    TNT_COLUMN(5),
    TNT_EXPLOSION(20),
    EXPERIENCE_EXPLOSION(45);

    private final int defaultValue;

    SpecialDropType(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int defaultValue() {
        return defaultValue;
    }
}
