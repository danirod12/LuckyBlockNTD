package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.logging.Level;

public class LBWorldGuard {

    public static boolean available = false;
    public static boolean updated = false;

    public static boolean isAvailable() {
        return available;
    }

    public static void register() {

        available = Misc.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin") != null &&
                Misc.getClass("com.sk89q.worldguard.protection.flags.Flag") != null;

        if (!available) {
            MvLogger.log(Level.INFO, "WorldGuard not found, support disabled");
            return;
        } else try {
            WorldGuardInstance.registerFlags();
        } catch (Throwable th) {
            MvLogger.log(Level.INFO, "WorldGuard not found, class mismatch");
        }

    }

    public static boolean canBreak(Block block) {

        if (!updated && !Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            available = false;
            return true;
        }
        if (!available) return true;

        return WorldGuardInstance.canBreak(block);

    }

    public static void setUnAvailable() {
        available = false;
    }

}
