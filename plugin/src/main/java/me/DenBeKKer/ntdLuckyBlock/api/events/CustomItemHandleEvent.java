package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomItemHandleEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final BekkerItemStack stack;
    private final Event event;
    private final boolean r;
    private boolean c = false;

    public CustomItemHandleEvent(BekkerItemStack stack, Event event, boolean registered) {
        this.stack = stack;
        this.event = event;
        this.r = registered;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public BekkerItemStack getItem() {
        return stack;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isRegistered() {
        return r;
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    @Override
    public void setCancelled(boolean cancel) {
        c = cancel;
    }
}
