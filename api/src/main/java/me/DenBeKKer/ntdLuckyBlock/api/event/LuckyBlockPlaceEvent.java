package me.DenBeKKer.ntdLuckyBlock.api.event;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;

public class LuckyBlockPlaceEvent extends CancellableEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final BlockPlaceEvent event;
    private final Block block;
    private final Player player;
    private final LuckyBlockKey key;

    public LuckyBlockPlaceEvent(BlockPlaceEvent event, Block block, Player player, LuckyBlockKey key) {
        this.event = event;
        this.block = block;
        this.player = player;
        this.key = key;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public BlockPlaceEvent getEvent() {
        return event;
    }

    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
    }

    public LuckyBlockKey getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
