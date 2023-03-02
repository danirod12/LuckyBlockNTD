package me.DenBeKKer.ntdLuckyBlock.listener;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class LightSourceListener implements Listener {

    private final LBMain instance;

    public LightSourceListener(LBMain instance) {
        this.instance = instance;
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            for (World world : Bukkit.getWorlds()) {
                updateChunks(world.getLoadedChunks());
            }
        }, Bukkit.getPluginManager().getPlugins().length * 2L + 100L);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            updateChunks(event.getChunk());
        }, 20L);
    }

    private void updateChunks(Chunk... chunks) {
        for (Chunk chunk : chunks) {
            Chunk clone = chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ());
            for (Entity entity : clone.getEntities()) {
                if (LuckyBlockAPI.searchByEntity(entity) != null) {
                    entity.setFireTicks(Integer.MAX_VALUE);
                }
            }
        }
    }

}
