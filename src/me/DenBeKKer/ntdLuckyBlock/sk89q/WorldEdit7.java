package me.DenBeKKer.ntdLuckyBlock.sk89q;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.DenBeKKer.ntdLuckyBlock.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockNotLoadedException;

public class WorldEdit7 implements IWorldEdit {
	
	private static StateFlag BREAK;
	
	public void paste(File file, Block obj, boolean a) {
		
		if(LBMain.isDebug()) LBMain.debug("Loading clipboard");
		Clipboard clipboard = null;
		
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
		    clipboard = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
			LBMain.log(Level.SEVERE, "Schematic " + file.getPath() + " not found or something went wrong");
		}
		
		if(clipboard == null) {
			LBMain.log(Level.SEVERE, "Schematic " + file.getPath() + " not found or something went wrong");
			return;
		}
		
		if(LBMain.isDebug()) LBMain.debug("Performing clipboard");
		try (EditSession editSession = createEditSession(obj.getWorld())) {
			ClipboardHolder holder = new ClipboardHolder(clipboard);
		    Operation operation = holder
		            .createPaste(editSession)
		            .to(BukkitAdapter.asBlockVector(obj.getLocation()))
		            .build();
		    Operations.complete(operation);
		    
		    if(clipboard instanceof BlockArrayClipboard) {
		    	Region region = clipboard.getRegion();
		    	BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
	            Vector3 realTo = BukkitAdapter.asVector(obj.getLocation()).add(holder.getTransform().apply(clipboardOffset.toVector3()));
	            Vector3 max = realTo.add(holder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
	            
	            Bukkit.getScheduler().runTaskLater(LBMain.getInstance(), () -> 
	            	formatPastedSchematic(file, obj.getWorld(), new CuboidRegion(realTo.toBlockPoint(), max.toBlockPoint()), a), 1);
	            
		    }
		    
		} catch (WorldEditException e) {
			e.printStackTrace();
			LBMain.log(Level.SEVERE, "Something went wrong");
		}
		
	}
	
	private void formatPastedSchematic(File file, World world, CuboidRegion region, boolean a) {
		
		for(BlockVector3 vector : region) {
	    	Block block = world.getBlockAt(vector.getX(), vector.getY(), vector.getZ());
	    	
	    	if(LBMain.getInstance().factory.isOakSign(block.getType())) {
	    		Sign sign = (Sign) block.getState();
	    		String[] lines = sign.getLines();
	    		if(lines[0].equalsIgnoreCase("[ntdluckyblock]")) {
	    			
	    			LuckyBlockType type = lines[1].equalsIgnoreCase("random") ? LuckyBlockType.random(true) : LuckyBlockType.parse(lines[1]);
	    			
	    			if(type == null) {
	    				LBMain.log(Level.WARNING, "LuckyBlockType " + lines[1].toUpperCase()
	    						+ " from schematic " + file.getName() + " not found");
	    			} else {
	    				
	    				try {
							LuckyBlockAPI.placeLuckyBlock(block, type);
						} catch (LuckyBlockNotLoadedException e) {
							LBMain.log(Level.WARNING, "LuckyBlockType " + lines[1].toUpperCase()
		    						+ " from schematic " + file.getName() + " not loaded");
						}
	    				
	    			}
	    			
	    		}
	    	} else if(a && block.isLiquid()) {
	    		Material item = block.getType();
	    		block.setType(Material.AIR);
	    		block.setType(item);
	    	}
	    }
		
	}
	
	@SuppressWarnings("deprecation")
	private EditSession createEditSession(World world) {
		try {
			return WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
		} catch(Throwable th) {
			return WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1);
		}
	}
	
	public boolean canBreak(Block block) {
		
		ApplicableRegionSet set = //worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location);
       		 WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld())).getApplicableRegions(
       				 BukkitAdapter.asBlockVector(block.getLocation()));
		
		if(set == null) return true;
		
		for(ProtectedRegion entry : set) {
			if(entry != null && entry.getFlags() != null && entry.getFlags().get(BREAK) != null &&
					entry.getFlags().get(BREAK) == StateFlag.State.DENY) return false;
		}
		return true;
		
	}
	
	public void registerFlags() {
		
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
	    try {
	        StateFlag flag = new StateFlag("ntd-lb-break", true);
	        registry.register(flag);
	        BREAK = flag;
	    } catch (FlagConflictException e) {
	        Flag<?> existing = registry.get("ntd-lb-break");
	        if (existing instanceof StateFlag) {
	        	BREAK = (StateFlag) existing;
	        } else {
	            LBMain.log(Level.INFO, "WorldGuard flag ntd-lb-break already found and cant be enjected");
	        }
	    } catch(IllegalStateException e) {
            LBMain.log(Level.INFO, "WorldGuard flags cant be registered at this time (Only on server startup available)");
	    }
		
	}
	
}
