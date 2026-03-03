package me.DenBeKKer.ntdLuckyBlock.variables.world;

import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

public class WorldsList {

    private final boolean a;
    private final List<String> b;
    private boolean c, d;

    public WorldsList(String t, List<String> l) {

        if (t == null || t.equalsIgnoreCase("disabled")) {
            this.a = false;
            this.b = null;
        } else {
            this.a = t.equalsIgnoreCase("whitelist");
            this.b = l.stream().map(String::toLowerCase).collect(Collectors.toList());
        }

    }

    public boolean getBreakNoDrop() {
        return c;
    }

    public void setBreakNoDrop(boolean b) {
        this.c = b;
    }

    public boolean getPlaceAdmins() {
        return d;
    }

    public void setPlaceAdmins(boolean b) {
        this.d = b;
    }

    public boolean allowed_break(World w) {
        return b == null || (a == b.contains(w.getName().toLowerCase()));
    }

}
