package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class CustomItemFactoryReloadEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    private final List<BekkerItemStack> items;
    private final boolean preloading;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CustomItemFactoryReloadEvent(List<BekkerItemStack> items, boolean preloading) {
        this.items = items;
        this.preloading = preloading;
    }

    public List<BekkerItemStack> getLoadedItems() { return items; }

    /**
     *
     * @return True - You should register custom items. False - You should only listen for added items
     */
    public boolean isPreLoaded() {
        return preloading;
    }

}
