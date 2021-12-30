package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import java.util.logging.Level;

import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class LBWorldGuard {
	
	public static boolean available = false;
	public static boolean updated = false;
	
	public static boolean isAvailable() { return available; }
	
	public static void register() {
		
		available = getClassZ("com.sk89q.worldguard.bukkit.WorldGuardPlugin") != null &&
				getClassZ("com.sk89q.worldguard.protection.flags.Flag") != null;
		
		if(!available) {
			MvLogger.log(Level.INFO, "WorldGuard not found, support disabled");
			return;
		} else try {
			WorldGuardInstance.registerFlags();
		} catch(Throwable th) {
			MvLogger.log(Level.INFO, "WorldGuard not found, class mismatch");
		}
		
	}

	public static Class<?> getClassZ(String s) {
		try {
			return Class.forName(s);
		} catch(ClassNotFoundException exception) {
			return null;
		}
	}

	public static boolean canBreak(Block block) {
		
		if(!updated && !Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			available = false;
			return true;
		}
		if(!available) return true;
		
		return WorldGuardInstance.canBreak(block);
		
	}
	
	public static void setUnAvailable() { available = false; }
	
}
