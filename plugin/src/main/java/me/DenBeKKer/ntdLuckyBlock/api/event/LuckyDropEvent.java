package me.DenBeKKer.ntdLuckyBlock.api.event;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LuckyDropEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final LuckyDrop drop;
    private final Player player;
    private final LBMain.LuckyBlockType source;
    private boolean c = false;

    public LuckyDropEvent(LBMain.LuckyBlockType source, LuckyDrop drop, Player target) {
        this.drop = drop;
        this.player = target;
        this.source = source;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public LBMain.LuckyBlockType getSource() {
        return source;
    }

    public Player getPlayer() {
        return player;
    }

    public LuckyDrop getDrop() {
        return drop;
    }

    public boolean isSpecial() {
        return getSpecialType() != null;
    }

    public LuckyDrop.Special getSpecialType() {
        return LuckyDrop.Special.parse(drop);
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.c = cancel;
    }

}
