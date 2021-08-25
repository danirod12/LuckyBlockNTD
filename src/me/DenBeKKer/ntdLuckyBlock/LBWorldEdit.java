package me.DenBeKKer.ntdLuckyBlock;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import me.DenBeKKer.ntdLuckyBlock.sk89q.WorldEdit6;
import me.DenBeKKer.ntdLuckyBlock.sk89q.WorldEdit7;

public class LBWorldEdit {
	
	private static IWorldEdit worldedit = null;
	private static boolean fawe = false;
	
	static {
		
		try {
			
			Class.forName("com.sk89q.worldedit.WorldEdit");
			
			try {
				
				Class.forName("com.sk89q.worldedit.math.Vector2");
				worldedit = (IWorldEdit) new WorldEdit7();
				if(LBMain.isDebug())
					LBMain.debug("IWorldEdit -> WorldEdit7");
				
			} catch(Throwable old) {
				worldedit = (IWorldEdit) new WorldEdit6();
				if(LBMain.isDebug())
					LBMain.debug("IWorldEdit -> WorldEdit6");
			}
			
		} catch(Throwable t) {
			// WorldEdit not found
		}
		
		fawe = getClass("com.fastasyncworldedit.bukkit.FaweBukkit") != null ||
				getClass("com.boydti.fawe.Fawe") != null;
 		if(fawe) {
			Bukkit.getLogger().log(Level.INFO, "FastAsyncWorldEdit support enabled");
 		} else if(LBMain.isDebug())
			LBMain.debug("FAWE not found");
		
	}
	
	private static Class<?> getClass(String path) {
		try {
			return Class.forName(path);
		} catch(Throwable th) { return null; }
	}
	
	public static boolean isFAWE() { return fawe; }
	
	public static boolean isPlatformAvailable() {
		
		if(!Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled())
			worldedit = null;
		return worldedit != null;
		
	}
	
	public static IWorldEdit getPlatform() throws Exception {
		if(worldedit == null) throw new Exception("IWorldEdit was not loaded. Maybe server WorldEdit version is unsupported");
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
		
		if(fawe) {
			Bukkit.getScheduler().runTaskLater(LBMain.getInstance(), () -> worldedit.paste(file, object, true), 1);
			return;
		}
		
		worldedit.paste(file, object, false);
	}
	
}
