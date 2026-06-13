package com.github.danirod12.luckyblock.listener;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

public class EntityLoadListener implements Listener {

    private final Plugin plugin;
    private final LuckyBlockEngine engine;

    public EntityLoadListener(Plugin plugin, LuckyBlockEngine engine) {
        this.plugin = plugin;
        this.engine = engine;
    }

    @SuppressWarnings({"unused"})
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) {
            return;
        }
        Location chunkLoc = event.getChunk().getBlock(8, 0, 8).getLocation();
        SchedulerManager.runLaterAt(plugin, chunkLoc, () -> {
            updateChunks(event.getChunk());
        }, 10L);
    }

    public void updateAllLoaded() {
        for (World world : Bukkit.getWorlds()) {
            updateChunks(world.getLoadedChunks());
        }
    }

    public void updateChunks(Chunk... chunks) {
        for (Chunk chunk : chunks) {
            if (!chunk.isLoaded()) {
                continue;
            }
            Location chunkLoc = chunk.getBlock(8, 0, 8).getLocation();
            SchedulerManager.runAtIfFolia(LuckyBlockAPI.getInstance(), chunkLoc, () -> {

                if (!chunk.isLoaded()) {
                    return;
                }

                for (Entity entity : chunk.getEntities()) {
                    if (engine.searchByEntity(entity) != null) {
                        if (engine.getConfigHolder().lightSource) {
                            entity.setFireTicks(Integer.MAX_VALUE);
                        } else {
                            entity.setFireTicks(0);
                        }
                        // migration for pre-v2.4
                        if (entity instanceof ArmorStand) {
                            ArmorStand stand = (ArmorStand) entity;
                            if (!stand.isMarker()) {
                                stand.setMarker(true);
                            }
                        }
                    }
                }
            });
        }
    }
}
