package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class EntityDrop implements LuckyDrop {
	
	@SerializedName(value = "entity")
	private EntityType entity;
	@SerializedName(value = "amount")
	private int amount;
	
	public EntityDrop(EntityType entity, int amount) {
		this.entity = entity;
		this.amount = amount;
	}
	
	@Override
	public void execute(Block b, Player target) { execute(b); }
	
	@Override
	public void execute(Block b) {
		for(int i = 0; i < amount; i++)
			b.getWorld().spawnEntity(b.getLocation().add(0.5, 1, 0.5), entity);
	}
	
}
