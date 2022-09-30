package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LuckyBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Block block;
    private final Player player;
    private final LuckyBlock luckyblock;
    private boolean ignore = false, c = false;
    private boolean drop = true;

    public LuckyBlockBreakEvent(Block b, Player p, LuckyBlock lb) {
        block = b;
        player = p;
        luckyblock = lb;
    }

    public LuckyBlockBreakEvent(Block b, LuckyBlock lb) {
        this(b, null, lb);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isTargetable() {
        return player != null;
    }

    public Player getPlayer() {
        return player;
    }

    public LuckyBlock getLuckyBlock() {
        return luckyblock;
    }

    public void setIgnoreCancelled() {
        ignore = true;
    }

    @Override
    public boolean isCancelled() {
        return ignore || c;
    }

    @Override
    public void setCancelled(boolean c) {
        this.c = c;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

}
