package me.DenBeKKer.ntdLuckyBlock.api.customitem;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class HitEvent extends Event {

    private final Entity damager;
    private final Entity victim;
    private final Type type;

    public HitEvent(Entity damager, Entity victim, Type type) {
        this.damager = damager;
        this.victim = victim;
        this.type = type;
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
