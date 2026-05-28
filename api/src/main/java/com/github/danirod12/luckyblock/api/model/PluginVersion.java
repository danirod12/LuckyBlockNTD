package com.github.danirod12.luckyblock.api.model;

/**
 * Represents the version of the plugin, which can be either free or premium (V2).
 */
public enum PluginVersion {

    FREE,
    PREMIUM,

    ;

    public boolean isPremium() {
        return this != FREE;
    }

    public boolean isFree() {
        return this == FREE;
    }

    public String getName() {
        return this.name();
    }

    public String getSimpleName() {
        return this.getName().toLowerCase();
    }

    public String getColoredSimpleName() {
        return (this == FREE ? "§a" : "§d") + this.getSimpleName();
    }

    @Deprecated
    public boolean hasJSONLoader() {
        return this == PREMIUM;
    }
}
