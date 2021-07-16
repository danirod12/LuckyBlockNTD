package me.DenBeKKer.ntdLuckyBlock;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import com.sk89q.worldedit.WorldEdit;

import me.DenBeKKer.ntdLuckyBlock.sk89q.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.sk89q.WorldEdit7;

public class LBWorldEdit {
	
	private static IWorldEdit worldedit = null;
	
	static {
		
		if(Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled()) {
			if(WorldEdit.getVersion().startsWith("7.")) worldedit = new WorldEdit7();
			//else if(WorldEdit.getVersion().startsWith("6.")) worldedit = new WorldEdit6();
			else LBMain.log(Level.SEVERE, "Your WorldEdit version is temporarily unsupported");
		}
		
	}
	
	public static IWorldEdit getPlatform() throws Exception {
		if(worldedit == null) throw new Exception("IWorldEdit was not loaded. Maybe server WorldEdit version is temporarily unsupported");
		return worldedit;
	}
	
	public static boolean canBreak(Block block) {
		if(worldedit == null) return true;
		return worldedit.canBreak(block);
	}
	
	public static void registerFlags() {
		if(worldedit == null) return;
		worldedit.registerFlags();
	}
	
	public static void paste(File file, Block object) {
		if(worldedit == null) return;
		worldedit.paste(file, object);
	}
	
}
