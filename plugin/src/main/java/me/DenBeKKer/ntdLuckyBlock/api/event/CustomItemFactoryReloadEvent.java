package me.DenBeKKer.ntdLuckyBlock.api.event;

import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class CustomItemFactoryReloadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final List<BekkerItemStack> items;
    private final Action action;

    public CustomItemFactoryReloadEvent(List<BekkerItemStack> items, Action action) {
        this.items = items;
        this.action = action;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public List<BekkerItemStack> getLoadedItems() {
        return items;
    }

    /**
     * @return PRELOAD - You should register custom items. LOADED - You should only listen for added items
     */
    public Action getAction() {
        return action;
    }

    public enum Action {
        PRELOAD, LOADED;
    }
}
