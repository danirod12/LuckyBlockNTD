package me.DenBeKKer.ntdLuckyBlock.sk89q;

import java.io.File;

import org.bukkit.block.Block;

public interface IWorldEdit {
	
	void paste(File file, Block obj);
	
	boolean canBreak(Block block);
	
	void registerFlags();
	
}
