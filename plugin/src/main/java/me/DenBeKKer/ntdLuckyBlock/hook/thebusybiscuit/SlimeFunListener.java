package me.DenBeKKer.ntdLuckyBlock.hook.thebusybiscuit;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SlimeFunListener implements Listener {

    @EventHandler
    public void place(BlockPlacerPlaceEvent e) {
        if (LuckyBlockAPI.isLuckyBlock(e.getBlockPlacer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void explode(ExplosiveToolBreakBlocksEvent e) {
        e.getAdditionalBlocks().removeIf(LuckyBlockAPI::isLuckyBlock);
    }

}
