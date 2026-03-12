package com.github.danirod12.luckyblock.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CancellableEvent extends Event implements Cancellable {

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
