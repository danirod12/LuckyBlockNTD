package me.DenBeKKer.ntdLuckyBlock.variables;

public class WorldListDataHandler {

    public WorldListDataHandler(boolean c, boolean d) {
        this.c = c;
        this.d = d;
    }

    private boolean c, d;

    public void setBreakNoDrop(boolean b) {
        this.c = b;
    }

    public void setPlaceAdmins(boolean b) {
        this.d = b;
    }

    public boolean getBreakNoDrop() {
        return c;
    }

    public boolean getPlaceAdmins() {
        return d;
    }

}
