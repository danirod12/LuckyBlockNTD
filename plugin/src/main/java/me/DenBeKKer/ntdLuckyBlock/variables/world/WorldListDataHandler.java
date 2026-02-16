package me.DenBeKKer.ntdLuckyBlock.variables.world;

public class WorldListDataHandler {

    private final boolean breakNoDrops;
    private final boolean placeAdmins;

    public WorldListDataHandler(boolean breakNoDrops, boolean placeAdmins) {
        this.breakNoDrops = breakNoDrops;
        this.placeAdmins = placeAdmins;
    }

    public boolean getBreakNoDrop() {
        return breakNoDrops;
    }

    public boolean getPlaceAdmins() {
        return placeAdmins;
    }
}
