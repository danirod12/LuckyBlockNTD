package me.DenBeKKer.ntdLuckyBlock.customitem;

import org.bukkit.entity.Entity;

public interface HitEvent {
	
	public enum Type {
		DAMAGER, VICTIM;
	}
	
	public void execute(Entity damager, Entity victim, Type type);
	
}
