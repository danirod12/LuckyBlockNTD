package me.DenBeKKer.ntdLuckyBlock.variables;

public enum PluginVersion {

    FREE,
    PREMIUM;

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
        return (this == FREE ? "\u00a7a" : "\u00a7d") + this.getSimpleName();
    }

    public boolean hasJSONLoader() {
        return this == PREMIUM;
    }

}
