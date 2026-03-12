package com.github.danirod12.luckyblock.api.event;

import com.github.danirod12.luckyblock.api.customitem.BekkerItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomItemHandleEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
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
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
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
