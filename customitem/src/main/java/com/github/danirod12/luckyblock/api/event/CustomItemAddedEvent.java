package com.github.danirod12.luckyblock.api.event;

import com.github.danirod12.luckyblock.api.customitem.BekkerItemStack;
import com.github.danirod12.luckyblock.api.model.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CustomItemAddedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final BekkerItemStack item;

    public CustomItemAddedEvent(BekkerItemStack item) {
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public BekkerItemStack getItem() {
        return item;
    }

    public Identifier getIdentifier() {
        return item.getIdentifier();
    }

    public ItemStack asItemStack() {
        return item;
    }

    public Plugin getPlugin() {
        final String name = item.getIdentifier().getTagValue().split("-")[0];
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(name)) {
                return plugin;
            }
        }
        return null;
    }
}
