package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;

public class EntitySpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final LBMain.LuckyBlockType r;
    private final Collection<Entity> i;
    private final Player p;

    public EntitySpawnEvent(LBMain.LuckyBlockType related, Collection<Entity> entity, Player player) {

        this.r = related;
        this.i = entity;
        this.p = player;

    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return p;
    }

    public LBMain.LuckyBlockType getSource() {
        return r;
    }

    public Collection<Entity> getEntity() {
        return i;
    }

}
