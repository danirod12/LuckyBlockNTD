package me.DenBeKKer.ntdLuckyBlock.customitem;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HitEvent extends Event {

    private Entity d;
    private Entity v;
    private Type type;

    public HitEvent(Entity damager, Entity victim, Type type) {
        this.d = damager;
        this.v = victim;
        this.type = type;
    }

    public Entity getDamager() {
        return d;
    }

    public Entity getVictim() {
        return v;
    }

    public Type getType() {
        return type;
    }

    public Entity getTrigger() {
        return type == Type.VICTIM ? getVictim() : getDamager();
    }

    @Override
    public HandlerList getHandlers() {
        throw new UnsupportedOperationException();
    }

    public enum Type {
        DAMAGER, VICTIM;
    }

}
