package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LuckyBlockPlaceEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Block block;
    private final Player player;
    private final LuckyBlockType luckyblock;
    private boolean c = false;

    public LuckyBlockPlaceEvent(Block b, Player p, LuckyBlockType type) {
        block = b;
        player = p;
        luckyblock = type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
    }

    public LuckyBlockType getLuckyBlockType() {
        return luckyblock;
    }

    public LuckyBlock getLuckyBlock() throws LuckyBlockNotLoadedException {
        if (luckyblock.get() == null)
            throw new LuckyBlockNotLoadedException(luckyblock);
        return luckyblock.get();
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    @Override
    public void setCancelled(boolean c) {
        this.c = c;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
