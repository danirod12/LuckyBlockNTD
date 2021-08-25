package me.DenBeKKer.ntdLuckyBlock;

import java.io.File;

import org.bukkit.block.Block;

public interface IWorldEdit {
	
	void paste(File file, Block obj, boolean activate);
	
	boolean canBreak(Block block);
	
	void registerFlags();
	
}
