package me.DenBeKKer.ntdLuckyBlock.listener;

import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
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
            for (Entity entity : chunk.getEntities()) {
                if (engine.searchByEntity(entity) != null) {
                    if (engine.getConfigHolder().lightSource) {
                        entity.setFireTicks(Integer.MAX_VALUE);
                    } else {
                        entity.setFireTicks(0);
                    }
                    // migration for pre-v2.4
                    ArmorStand stand = ((ArmorStand) entity);
                    if (!stand.isMarker()) {
                        stand.setMarker(true);
                    }
                }
            }
        }
    }
}
