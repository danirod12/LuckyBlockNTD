package me.DenBeKKer.ntdLuckyBlock.sk89q;

import java.io.File;
import java.util.logging.Level;

import me.DenBeKKer.ntdLuckyBlock.util.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

@SuppressWarnings("deprecation")
public class WorldEdit6 implements IWorldEdit {
	
	private WorldEditPlugin worldedit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
//	private StateFlag BREAK;
	
	@Override
	public void paste(File file, Block obj, boolean a) {
		
		try {
			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
			clipboard.paste(worldedit.getWorldEdit().getEditSessionFactory()
					 .getEditSession(new BukkitWorld(obj.getWorld()), -1), new Vector(obj.getX(), obj.getY(), obj.getZ()), true);
		} catch (Exception e) {
			e.printStackTrace();
			MvLogger.log(Level.SEVERE, "Something went wrong");
		}
		
	}
	
//	@Override
//	public boolean canBreak(Block block) {
//		
//		ApplicableRegionSet set = null;
//		
//		try {
//			Object container = worldguard.getClass().getMethod("getRegionContainer").invoke(worldguard);
//			container = container.getClass().getMethod("get", World.class).invoke(container, block.getWorld());
//			set = (ApplicableRegionSet) container.getClass().getMethod("getApplicableRegions", Location.class)
//					.invoke(container, block.getLocation());
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
//				| SecurityException e) {
//			e.printStackTrace();
//			return true;
//		}
//		
//		if(set == null) return true;
//		
//		for(ProtectedRegion entry : set) {
//			if(entry != null && entry.getFlags() != null && entry.getFlags().get(BREAK) != null &&
//					entry.getFlags().get(BREAK) == StateFlag.State.DENY) return false;
//		}
//		return true;
//		
//	}
//	
//	@Override
//	public void registerFlags() {
//		
////		FlagRegistry registry = worldguard.getRegionContainer().getInstance().getFlagRegistry();
//		
//		FlagRegistry registry = null;
//		
//		try {
//			
////			Object container = worldguard.getClass().getMethod("getRegionContainer").invoke(worldguard);
////			container = container.getClass().getMethod("getInstance").invoke(container);
////			registry = (FlagRegistry)container.getClass().getMethod("getFlagRegistry").invoke(container);
//			registry = (FlagRegistry) worldguard.getClass().getMethod("getFlagRegistry").invoke(worldguard);
//			
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
//				| SecurityException e1) {
//			e1.printStackTrace();
//			return;
//		}
//		
//		try {
//			StateFlag flag = new StateFlag("ntd-lb-break", true);
//			registry.register(flag);
//			BREAK = flag;
//		} catch (FlagConflictException e) {
//			Flag<?> existing = registry.get("ntd-lb-break");
//			if (existing instanceof StateFlag) {
//				BREAK = (StateFlag) existing;
//			} else {
//				LBMain.log(Level.INFO, "WorldGuard flag ntd-lb-break already found and cant be enjected");
//			}
//		} catch(IllegalStateException e) {
//			LBMain.log(Level.INFO, "WorldGuard flags cant be registered at this time (Only on server startup available)");
//		}
//		
//	}
	
}
