package com.github.danirod12.luckyblock.hook.thebusybiscuit;

import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SlimeFunListener implements Listener {

    private final LuckyBlockEngine engine;

    public SlimeFunListener(LuckyBlockEngine engine) {
        this.engine = engine;
    }

    @EventHandler
    public void onBlockPlacerPlace(BlockPlacerPlaceEvent event) {
        if (engine.isLuckyBlock(event.getBlockPlacer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosiveToolBreakBlocks(ExplosiveToolBreakBlocksEvent event) {
        event.getAdditionalBlocks().removeIf(engine::isLuckyBlock);
    }
}
