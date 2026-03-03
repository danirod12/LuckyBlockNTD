package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.util.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldEditProvider {

    private final Plugin plugin;
    @Getter
    private final File folder;
    private final IWorldEdit worldedit;
    private final boolean fawe;

    public WorldEditProvider(Plugin plugin, File folder, Logger logger) {
        this.plugin = plugin;
        this.folder = folder;
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit") ||
                Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            fawe = Misc.getClass("com.fastasyncworldedit.bukkit.FaweBukkit") != null ||
                    Misc.getClass("com.boydti.fawe.Fawe") != null;
            String pluginName = fawe ? "FastAsyncWorldEdit" : "WorldEdit";
            if (Misc.getClass("com.sk89q.worldedit.math.Vector2") != null) {
                worldedit = new WorldEdit7();
                logger.log(Level.INFO, "Using " + pluginName + " v6 (1.8-1.12) adapter");
            } else {
                worldedit = new WorldEdit6();
                logger.log(Level.INFO, "Using " + pluginName + " v7 (1.13+) adapter");
            }
        } else {
            worldedit = null;
            fawe = false;
        }
    }

    public boolean isPlatformAvailable() {
        return worldedit != null;
    }

    public IWorldEdit getPlatform() {
        return worldedit;
    }

    public void paste(File file, Block target, boolean ignoreAir) {
        if (worldedit == null)
            return;
        if (fawe) {
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> worldedit.paste(file, target, true, ignoreAir), 1);
        } else {
            worldedit.paste(file, target, false, ignoreAir);
        }
    }
}
