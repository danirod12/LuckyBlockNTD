package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.ExplosionableItems;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class ExperienceExplosionSpecial extends ExplosionableItems implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private int a;
	
	public ExperienceExplosionSpecial(int a) {
		this.a = a;
	}
	
	@Override
	public void execute(Block b, Player target) {
		throwExplosion(EntityType.THROWN_EXP_BOTTLE, target.getLocation().add(0, .5, 0), a);
	}
	
	@Override
	public void execute(Block b) {
		throwExplosion(EntityType.THROWN_EXP_BOTTLE, b.getLocation().add(.5, .5, .5), a);
	}
	
}
