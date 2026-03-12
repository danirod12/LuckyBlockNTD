package me.DenBeKKer.ntdLuckyBlock.api.event;

import lombok.Data;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
public class ItemSpawnEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LuckyDrop.Execution execution;
    private final Item drop;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
