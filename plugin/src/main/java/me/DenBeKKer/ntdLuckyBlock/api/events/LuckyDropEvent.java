package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LuckyDropEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private final LuckyDrop drop;
    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LuckyDropEvent(LuckyDrop drop, Player target) {
        this.drop = drop;
        this.player = target;
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

    private boolean c = false;

    @Override
    public void setCancelled(boolean cancel) {
        this.c = cancel;
    }

}
