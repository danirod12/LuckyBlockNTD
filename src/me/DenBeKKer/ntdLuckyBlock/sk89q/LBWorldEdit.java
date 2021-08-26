package me.DenBeKKer.ntdLuckyBlock.sk89q;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class LBWorldEdit {
	
	private static IWorldEdit worldedit = null;
	private static boolean fawe = false;
	
	static {
		
		if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit") ||
				Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
			
			if(LBMain.getClass("com.sk89q.worldedit.math.Vector2") != null) {
				worldedit = (IWorldEdit) new WorldEdit7();
				if(LBMain.isDebug())
					LBMain.debug("WorldEdit v6 (1.8 - 1.12)");
			} else {
				worldedit = (IWorldEdit) new WorldEdit6();
				if(LBMain.isDebug())
					LBMain.debug("WorldEdit v7 (1.13 - 1.17 and above)");
			}
			
			fawe = LBMain.getClass("com.fastasyncworldedit.bukkit.FaweBukkit") != null ||
					LBMain.getClass("com.boydti.fawe.Fawe") != null;
	 		if(fawe)
	 			LBMain.log(Level.INFO, "\u00a7aFastAsyncWorldEdit support enabled");
			
		}
		
	}
	
	public static boolean isFAWE() { return fawe; }
	
	public static boolean isPlatformAvailable() {
		
		if(!Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled() &&
				!Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit").isEnabled())
			worldedit = null;
		return worldedit != null;
		
	}
	
	public static IWorldEdit getPlatform() throws Exception {
		if(worldedit == null) throw new Exception("IWorldEdit was not loaded. Maybe server WorldEdit version is unsupported");
		return worldedit;
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
