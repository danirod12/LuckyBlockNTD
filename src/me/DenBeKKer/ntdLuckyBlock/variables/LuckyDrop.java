package me.DenBeKKer.ntdLuckyBlock.variables;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.EntityDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;

public interface LuckyDrop {
	
	public enum LuckyItemType { LUCKY_BLOCK_ITEM, ITEM, SPECIAL, ENTITY, COMMAND, CONSOLE, MESSAGE, SCHEMATIC; }
	
	public enum Special {
		
		PIG,
		LIGHTNING,
		WATER_BUCKET,
		DIAMOND_COLUMN,
		TNT_COLUMN,
		TNT_EXPLOSION,
		EXPERIENCE_EXPLOSION;
		
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
	
	public default void executeProtected(Block block, Player target) {
		
		try {
			if(target == null)
				execute(block);
			else execute(block, target);
		} catch(Throwable th) {
			if(LBMain.h()) {
				
				if(this instanceof EntityDrop && th.getMessage().contains("Cannot spawn an entity")) return;
				if(this instanceof ItemDrop && th.getMessage().toLowerCase().contains("air")) return;
				
			}
			th.printStackTrace();
		}
		
	}
	
	void execute(Block b, Player target);
	
	void execute(Block b);
	
}
