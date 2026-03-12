package com.github.danirod12.luckyblock.hook.sk89q;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldGuardProviderImpl implements WorldGuardProvider {

    private final StateFlag breakFlag;

    public WorldGuardProviderImpl(Logger logger) {
        FlagRegistry registry = null;
        try {
            registry = WorldGuard.getInstance().getFlagRegistry();
        } catch (Throwable throwable) {
            // Old WorldGuard version
            try {
                registry = (FlagRegistry) getWorldGuard().getClass()
                        .getMethod("getFlagRegistry").invoke(getWorldGuard());
            } catch (Exception exception) {
                throwable.printStackTrace();
                exception.printStackTrace();
            }
        }
        if (registry == null) {
            breakFlag = null;
            return;
        }

        StateFlag flag = null;
        try {
            flag = new StateFlag("ntd-lb-break", true);
            registry.register(flag);
        } catch (Exception exception) {
            if (exception instanceof IllegalStateException) {
                logger.log(Level.INFO, "You know, PlugMan is a bad thing...");
            }

            Flag<?> existing = registry.get("ntd-lb-break");
            if (existing instanceof StateFlag) {
                flag = (StateFlag) existing;
            } else {
                logger.log(Level.INFO, "WorldGuard flag was already injected, using it");
            }
        }
        breakFlag = flag;
    }

    @Override
    public boolean canBreak(Block block) {
        if (breakFlag == null) {
            return true;
        }

        ApplicableRegionSet set;
        try {
            set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()
                    .getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
        } catch (Throwable throwable) {
            // Old WorldGuard
            try {
                Object container = getWorldGuard().getClass().getMethod("getRegionContainer").invoke(getWorldGuard());
                container = container.getClass().getMethod("get", World.class).invoke(container, block.getWorld());
                set = (ApplicableRegionSet) container.getClass().getMethod("getApplicableRegions", Location.class)
                        .invoke(container, block.getLocation());
            } catch (Exception exception) {
                throwable.printStackTrace();
                exception.printStackTrace();
                return true;
            }
        }
        if (set == null) {
            return true;
        }

        for (ProtectedRegion entry : set) {
            if (entry.getFlags().getOrDefault(breakFlag, StateFlag.State.ALLOW) == StateFlag.State.DENY) {
                return false;
            }
        }
        return true;
    }

    private WorldGuardPlugin getWorldGuard() {
        return (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }
}
