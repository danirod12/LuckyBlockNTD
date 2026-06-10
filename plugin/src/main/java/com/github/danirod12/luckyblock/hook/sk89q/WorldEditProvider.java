package com.github.danirod12.luckyblock.hook.sk89q;

import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import com.github.danirod12.luckyblock.api.model.IWorldEdit;
import com.github.danirod12.luckyblock.api.provider.LuckyEngineProvider;
import com.github.danirod12.luckyblock.api.util.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;

public class WorldEditProvider {

    private final Plugin plugin;
    private final IWorldEdit worldedit;
    private final boolean fastAsyncWorldEdit;

    public WorldEditProvider(Plugin plugin, LuckyEngineProvider provider) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
                || Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            fastAsyncWorldEdit = JavaUtils.getClass("com.fastasyncworldedit.bukkit.FaweBukkit") != null
                    || JavaUtils.getClass("com.boydti.fawe.Fawe") != null;
            String pluginName = fastAsyncWorldEdit ? "FastAsyncWorldEdit" : "WorldEdit";
            if (JavaUtils.getClass("com.sk89q.worldedit.math.Vector2") != null) {
                worldedit = new WorldEdit7(this.plugin, provider);
                Bukkit.getLogger().log(Level.INFO, "[ntdLuckyBlock] Using " + pluginName + " v6 (1.8-1.12) adapter");
            } else {
                worldedit = new WorldEdit6();
                Bukkit.getLogger().log(Level.INFO, "[ntdLuckyBlock] Using " + pluginName + " v7 (1.13+) adapter");
            }
        } else {
            worldedit = null;
            fastAsyncWorldEdit = false;
        }
    }

    public boolean isPlatformAvailable() {
        return worldedit != null;
    }

    public void paste(File file, Block target, boolean ignoreAir) {
        if (worldedit == null) {
            return;
        }
        if (fastAsyncWorldEdit) {
            SchedulerManager.runLaterAt(plugin, target.getLocation(),
                    () -> worldedit.paste(file, target, /*true, */ignoreAir), 1);
        } else {
            worldedit.paste(file, target, /*false, */ignoreAir);
        }
    }
}
