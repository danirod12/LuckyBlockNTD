package me.DenBeKKer.ntdLuckyBlock.api.event;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class LuckyBlockBreakEvent extends CancellableEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Block block;
    private final Player player;
    private final LuckyBlock luckyBlock;
    private boolean drop = true;

    public LuckyBlockBreakEvent(Block block, Player player, LuckyBlock luckyBlock) {
        this.block = block;
        this.player = player;
        this.luckyBlock = luckyBlock;
    }

    public LuckyBlockBreakEvent(Block block, LuckyBlock luckyBlock) {
        this(block, null, luckyBlock);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isPlayerInvolved() {
        return player != null;
    }

    public Player getPlayer() {
        return player;
    }

    public LuckyBlock getKey() {
        return luckyBlock;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

}
