package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LBWorldEdit {

    private static IWorldEdit worldedit = null;
    private static boolean fawe = false;

    static {

        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit") ||
                Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {

            if (Misc.getClass("com.sk89q.worldedit.math.Vector2") != null) {
                worldedit = (IWorldEdit) new WorldEdit7();
                LBMain.debug("WorldEdit v6 (1.8 - 1.12)");
            } else {
                worldedit = (IWorldEdit) new WorldEdit6();
                LBMain.debug("WorldEdit v7 (1.13 - 1.17 and above)");
            }

            fawe = Misc.getClass("com.fastasyncworldedit.bukkit.FaweBukkit") != null ||
                    Misc.getClass("com.boydti.fawe.Fawe") != null;
            if (fawe) LBMain.log(Level.INFO, "\u00a7aFastAsyncWorldEdit support enabled");

        }

    }

    public static boolean isFAWE() {
        return fawe;
    }

    public static boolean isPlatformAvailable() {

        if (!Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled() &&
                !Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit").isEnabled())
            worldedit = null;
        return worldedit != null;

    }

    public static IWorldEdit getPlatform() throws Exception {
        if (worldedit == null)
            throw new Exception("IWorldEdit was not loaded. Maybe server WorldEdit version is unsupported");
        return worldedit;
    }

    public static void paste(File file, Block object) {
        if (worldedit == null) return;
        Bukkit.getScheduler().runTaskLater(LBMain.getInstance(), () -> forcePaste(file, object, fawe), 1);
    }

    private static void forcePaste(File file, Block block, boolean activate) {
        List<String> blacklist = LBMain.getInstance().worldeditMask;
        worldedit.paste(file, block, activate, blacklist == null ? new ArrayList<>() : blacklist);
    }
}
