package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class PigSpecial implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private int a;
	
	public PigSpecial(int a) {
		this.a = a;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(Block b, Player target) {
		Pig current = null;
		for(int i = 0; i < a; i++) {
			Pig pig = (Pig) b.getWorld().spawnEntity(target.getLocation(), EntityType.PIG);
			pig.setCanPickupItems(false);
			pig.setRemoveWhenFarAway(true);
			if(current != null) 
				current.setPassenger(pig);
			current = pig;
		}
		current.setPassenger(target);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(Block b) {
		Pig current = null;
		for(int i = 0; i < a; i++) {
			Pig pig = (Pig) b.getWorld().spawnEntity(b.getLocation().add(0.5, 0.4, 0.5), EntityType.PIG);
			pig.setCanPickupItems(false);
			pig.setRemoveWhenFarAway(true);
			if(current != null) 
				current.setPassenger(pig);
			current = pig;
		}
	}
	
}
