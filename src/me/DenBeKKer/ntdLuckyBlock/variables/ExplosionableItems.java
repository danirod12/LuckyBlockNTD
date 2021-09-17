package me.DenBeKKer.ntdLuckyBlock.variables;

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
	
	protected void throwExplosion(EntityType type, Location location) {
		throwExplosion(type, location, 1);
	}
	
	protected void throwExplosion(EntityType type, Location location, int amount) {
		for(int i = 0; i < amount; i++)
			throwExplosion(location.getWorld().spawnEntity(location, type));
	}
	
	protected Entity[] create(EntityType type, Location location, int amount) {
		
		Entity[] en = new Entity[amount];
		for(int i = 0 ; i < amount; i++)
			en[i] = location.getWorld().spawnEntity(location, type);
		return en;
		
	}
	
}
