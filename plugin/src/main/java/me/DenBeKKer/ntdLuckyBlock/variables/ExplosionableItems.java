package me.DenBeKKer.ntdLuckyBlock.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class ExplosionableItems {
	
	protected void throwExplosion(Entity... collection) {
		
		for(Entity entity : collection)
			throwExplosion(entity);
		
	}
	
	protected void throwExplosion(Entity entity) {
		entity.setVelocity(new Vector(ThreadLocalRandom.current().nextDouble(-.25, .25),
				ThreadLocalRandom.current().nextDouble(.75, 1.25),
				ThreadLocalRandom.current().nextDouble(-.25, .25)));
	}
	
	protected Collection<Entity> throwExplosion(EntityType type, Location location) {
		return throwExplosion(type, location, 1);
	}
	
	protected Collection<Entity> throwExplosion(EntityType type, Location location, int amount) {
		Collection<Entity> collection = new ArrayList<>();
		for(int i = 0; i < amount; i++) {
			Entity entity = location.getWorld().spawnEntity(location, type);
			collection.add(entity);
			throwExplosion(entity);
		}
		return collection;
	}
	
	protected Entity[] create(EntityType type, Location location, int amount) {
		
		Entity[] en = new Entity[amount];
		for(int i = 0 ; i < amount; i++)
			en[i] = location.getWorld().spawnEntity(location, type);
		return en;
		
	}
	
}
