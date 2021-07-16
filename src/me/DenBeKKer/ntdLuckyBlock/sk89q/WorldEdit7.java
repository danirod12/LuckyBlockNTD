package me.DenBeKKer.ntdLuckyBlock.sk89q;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

import org.bukkit.block.Block;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class WorldEdit7 implements IWorldEdit {
	
	private static StateFlag BREAK;
	
	public void paste(File file, Block obj) {
		
		if(LBMain.getDebug()) LBMain.debug("Loading clipboard");
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
		
		if(LBMain.getDebug()) LBMain.debug("Performing clipboard");
		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(obj.getWorld()))) {
		    Operation operation = new ClipboardHolder(clipboard)
		            .createPaste(editSession)
		            .to(BukkitAdapter.asBlockVector(obj.getLocation()))
		            .build();
		    Operations.complete(operation);
		} catch (WorldEditException e) {
			e.printStackTrace();
			LBMain.log(Level.SEVERE, "Something went wrong");
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
