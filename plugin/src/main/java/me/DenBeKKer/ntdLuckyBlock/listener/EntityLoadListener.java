package me.DenBeKKer.ntdLuckyBlock.listener;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class EntityLoadListener implements Listener {

    private final LBMain instance;

    public EntityLoadListener(LBMain instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk())
            return;
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            updateChunks(event.getChunk());
        }, 20L);
    }

    public void updateAllLoaded() {
        for (World world : Bukkit.getWorlds()) {
            updateChunks(world.getLoadedChunks());
        }
    }

    public void updateChunks(Chunk... chunks) {
        for (Chunk chunk : chunks) {
            for (Entity entity : chunk.getEntities()) {
                if (LuckyBlockAPI.searchByEntity(entity) != null) {
                    if (instance.isLightSource()) {
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
