package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class CustomItemFactoryReloadEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    private final List<BekkerItemStack> items;
    private final Action action;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Action {
        PRELOAD, LOADED;
    }

    public CustomItemFactoryReloadEvent(List<BekkerItemStack> items, Action action) {
        this.items = items;
        this.action = action;
    }

    public List<BekkerItemStack> getLoadedItems() { return items; }

    /**
     *
     * @return PRELOAD - You should register custom items. LOADED - You should only listen for added items
     */
    public Action getAction() {
        return action;
    }

}
