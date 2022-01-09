package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.ExplosionableItems;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class ExperienceExplosionSpecial extends ExplosionableItems implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private final int a;
	
	public ExperienceExplosionSpecial(int a) {
		this.a = a;
	}

	public int getAmount() { return a; }

	@Override
	public void execute(LBMain.LuckyBlockType related, Block block, Player player) {

		throwExplosion(EntityType.THROWN_EXP_BOTTLE,
				player == null ? block.getLocation().add(.5, .5, .5) : player.getLocation().clone().add(.0, .5, .0), a);

	}

}
