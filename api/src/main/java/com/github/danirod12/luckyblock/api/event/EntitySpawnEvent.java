package com.github.danirod12.luckyblock.api.event;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import lombok.Data;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Data
public class EntitySpawnEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LuckyDrop.Execution execution;
    private final List<Entity> list;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
