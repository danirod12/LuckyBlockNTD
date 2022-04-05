package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.IWorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.io.File;
import java.util.logging.Level;

import static me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldGuard.getClassZ;

public class LBWorldEdit {

	private static IWorldEdit worldedit = null;
	private static boolean fawe = false;
	
	static {
		
		if(Bukkit.getPluginManager().isPluginEnabled("WorldEdit") ||
				Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
			
			if(getClassZ("com.sk89q.worldedit.math.Vector2") != null) {
				worldedit = (IWorldEdit) new WorldEdit7();
					LBMain.debug("WorldEdit v6 (1.8 - 1.12)");
			} else {
				worldedit = (IWorldEdit) new WorldEdit6();
					LBMain.debug("WorldEdit v7 (1.13 - 1.17 and above)");
			}

			if(fawe = getClassZ("com.fastasyncworldedit.bukkit.FaweBukkit") != null ||
					getClassZ("com.boydti.fawe.Fawe") != null) LBMain.log(Level.INFO, "\u00a7aFastAsyncWorldEdit support enabled");
			
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
