package me.DenBeKKer.ntdLuckyBlock.variables;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.events.LuckyDropEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.variables.drop.EntityDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;

public interface LuckyDrop {
	
	public enum LuckyItemType { LUCKY_BLOCK_ITEM, ITEM, SPECIAL, ENTITY, COMMAND, CONSOLE, MESSAGE, SCHEMATIC, CUSTOM_ITEM; }
	
	public enum Special {
		
		PIG,
		LIGHTNING,
		WATER_BUCKET,
		DIAMOND_COLUMN,
		TNT_COLUMN,
		TNT_EXPLOSION,
		EXPERIENCE_EXPLOSION;

		public static Special parse(LuckyDrop drop) {

			if (drop instanceof DiamondColumnSpecial) {
				return DIAMOND_COLUMN;
			} else if(drop instanceof ExperienceExplosionSpecial) {
				return EXPERIENCE_EXPLOSION;
			} else if(drop instanceof LightningSpecial) {
				return LIGHTNING;
			} else if(drop instanceof PigSpecial) {
				return PIG;
			} else if(drop instanceof TntColumnSpecial) {
				return TNT_COLUMN;
			} else if(drop instanceof TntExplosionSpecial) {
				return TNT_EXPLOSION;
			} else if(drop instanceof WaterBucketSpecial) {
				return WATER_BUCKET;
			}

			return null;

		}

		public int defaultValue() {
			switch(this) {
				case LIGHTNING: return 3;
				case PIG: return 4;
				case WATER_BUCKET: return 64;
				case TNT_COLUMN: return 5;
				case TNT_EXPLOSION: return 20;
				case EXPERIENCE_EXPLOSION: return 45;
				default: return 1;
			}
		}
	
	}
	
	default void executeProtected(Block block, Player target) {

		LuckyDropEvent event = new LuckyDropEvent(this, target);
		Bukkit.getPluginManager().callEvent(event);

		if(event.isCancelled()) return;

		try {
			if(target == null)
				execute(block);
			else execute(block, target);
		} catch(Throwable th) {
			if(LBMain.h() && th.getMessage() != null) {
				
				if(this instanceof EntityDrop && th.getMessage().contains("Cannot spawn an entity")) return;
				if(this instanceof ItemDrop && th.getMessage().toLowerCase().contains("air")) return;
				
			}
			th.printStackTrace();
		}
		
	}
	
	void execute(Block b, Player target);
	
	void execute(Block b);
	
}
