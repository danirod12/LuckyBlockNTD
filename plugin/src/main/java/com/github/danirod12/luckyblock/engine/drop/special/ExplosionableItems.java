package com.github.danirod12.luckyblock.engine.drop.special;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExplosionableItems {

    protected void throwExplosion(Entity... collection) {
        for (Entity entity : collection) {
            throwExplosion(entity);
        }
    }

    protected void throwExplosion(Entity entity) {
        entity.setVelocity(new Vector(ThreadLocalRandom.current().nextDouble(-.25, .25),
                ThreadLocalRandom.current().nextDouble(.75, 1.25),
                ThreadLocalRandom.current().nextDouble(-.25, .25)));
    }

    protected List<Entity> throwExplosion(EntityType type, Location location) {
        return throwExplosion(type, location, 1);
    }

    protected List<Entity> throwExplosion(EntityType type, Location location, int amount) {
        List<Entity> collection = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Entity entity = location.getWorld().spawnEntity(location, type);
            collection.add(entity);
            throwExplosion(entity);
        }
        return collection;
    }

    protected Entity[] create(EntityType type, Location location, int amount) {
        Entity[] en = new Entity[amount];
        for (int i = 0; i < amount; i++) {
            en[i] = location.getWorld().spawnEntity(location, type);
        }
        return en;
    }
}
