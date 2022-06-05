package me.DenBeKKer.ntdLuckyBlock.customitem;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public final class ItemEvent<E> {

    public static final ItemEvent<PlayerInteractEvent> INTERACT = new ItemEvent<>(PlayerInteractEvent.class);
    public static final ItemEvent<BlockPlaceEvent> PLACE = new ItemEvent<>(BlockPlaceEvent.class);
    public static final ItemEvent<BlockBreakEvent> BREAK = new ItemEvent<>(BlockBreakEvent.class);
    public static final ItemEvent<HitEvent> HIT = new ItemEvent<>(HitEvent.class);
    public static final ItemEvent<PlayerItemConsumeEvent> CONSUME = new ItemEvent<>(PlayerItemConsumeEvent.class);
    private final Class<?> clazz;

    public ItemEvent(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getInstance() {
        return clazz;
    }

}
