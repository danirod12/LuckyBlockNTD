package me.DenBeKKer.ntdLuckyBlock.api.event;

import lombok.Data;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
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
