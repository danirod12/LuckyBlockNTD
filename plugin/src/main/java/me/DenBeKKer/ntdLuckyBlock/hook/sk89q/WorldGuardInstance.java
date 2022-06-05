package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.logging.Level;

public class WorldGuardInstance {

    private static StateFlag BREAK = null;

    public static void registerFlags() {

        FlagRegistry registry = null;

        try {
            registry = WorldGuard.getInstance().getFlagRegistry();
        } catch (Throwable th1) {
            // Old WorldGuard version
            try {
                registry = (FlagRegistry) getWorldGuard().getClass().getMethod("getFlagRegistry").invoke(getWorldGuard());
            } catch (Exception e1) {
                th1.printStackTrace();
                e1.printStackTrace();
                return;
            }
        }

        if (registry == null) {
            LBWorldGuard.setUnAvailable();
            return;
        }

        try {
            StateFlag flag = new StateFlag("ntd-lb-break", true);
            registry.register(flag);
            BREAK = flag;
        } catch (Exception e) {

            if (e instanceof IllegalStateException) {
                MvLogger.log(Level.INFO, "WorldGuard flags cant be registered at this time (Only on server startup available)");
                return;
            }

            Flag<?> existing = registry.get("ntd-lb-break");
            if (existing instanceof StateFlag) {
                BREAK = (StateFlag) existing;
            } else {
                MvLogger.log(Level.INFO, "WorldGuard flag ntd-lb-break already found and cant be enjected");
            }

        }

    }

    public static boolean canBreak(Block block) {

        if (BREAK == null) return true;

        ApplicableRegionSet set = null;

        try {
            set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()
                    .getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
        } catch (Throwable th1) {
            // Old WorldGuard
            try {
                Object container = getWorldGuard().getClass().getMethod("getRegionContainer").invoke(getWorldGuard());
                container = container.getClass().getMethod("get", World.class).invoke(container, block.getWorld());
                set = (ApplicableRegionSet) container.getClass().getMethod("getApplicableRegions", Location.class)
                        .invoke(container, block.getLocation());
            } catch (Exception e) {
                th1.printStackTrace();
                e.printStackTrace();
                return true;
            }
        }

        if (set == null) return true;

        for (ProtectedRegion entry : set) {
            if (entry != null && entry.getFlags() != null && entry.getFlags().get(BREAK) != null &&
                    entry.getFlags().get(BREAK) == StateFlag.State.DENY) return false;
        }
        return true;

    }

    private static WorldGuardPlugin getWorldGuard() {
        return (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

}
