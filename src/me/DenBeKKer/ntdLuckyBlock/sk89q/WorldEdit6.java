package me.DenBeKKer.ntdLuckyBlock.sk89q;

import java.io.File;

import org.bukkit.block.Block;

public class WorldEdit6 implements IWorldEdit {

	@Override
	public void paste(File file, Block obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canBreak(Block block) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerFlags() {
		// TODO Auto-generated method stub
		
	}
	
//	private WorldGuardPlugin worldguard = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
//	
//	@SuppressWarnings("deprecation")
//	@Override
//	public void paste(File file, Block obj) {
//		try {
//			MCEditSchematicFormat.getFormat(file).load(file).paste(worldguard.getWorldEdit().getWorldEdit().getEditSessionFactory()
//					 .getEditSession(new BukkitWorld(obj.getWorld()), -1), new Vector(obj.getX(), obj.getY(), obj.getZ()), true);
//		} catch (Exception e) {
//			e.printStackTrace();
//			LBMain.log(Level.SEVERE, "Something went wrong");
//		}
//		
//	}
//	
//	@Override
//	public boolean canBreak(Block block) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//	
//	@Override
//	public void registerFlags() {
////		
////		FlagRegistry registry = worldguard.getRegionContainer()..getInstance().getFlagRegistry();
////	    try {
////	        StateFlag flag = new StateFlag("ntd-lb-break", true);
////	        registry.register(flag);
////	        BREAK = flag;
////	    } catch (FlagConflictException e) {
////	        Flag<?> existing = registry.get("ntd-lb-break");
////	        if (existing instanceof StateFlag) {
////	        	BREAK = (StateFlag) existing;
////	        } else {
////	            LBMain.log(Level.INFO, "WorldGuard flag ntd-lb-break already found and cant be enjected");
////	        }
////	    } catch(IllegalStateException e) {
////            LBMain.log(Level.INFO, "WorldGuard flags cant be registered at this time (Only on server startup available)");
////	    }
////		
//	}
	
}
