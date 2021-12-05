package me.DenBeKKer.ntdLuckyBlock.sk89q;

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
import me.DenBeKKer.ntdLuckyBlock.util.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class WorldEdit7 implements IWorldEdit {

	public void paste(File file, Block obj, boolean a) {

		Clipboard clipboard = null;
		
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
		    clipboard = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
			MvLogger.log(Level.SEVERE, "Schematic " + file.getPath() + " not found or something went wrong");
		}
		
		if(clipboard == null) {
			MvLogger.log(Level.SEVERE, "Schematic " + file.getPath() + " not found or something went wrong");
			return;
		}

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
	            
	            Bukkit.getScheduler().runTaskLater(MvLogger.getInstance(), () ->
	            	formatPastedSchematic(obj.getWorld(), new CuboidRegion(realTo.toBlockPoint(), max.toBlockPoint()), a), 1);
	            
		    }
		    
		} catch (WorldEditException e) {
			e.printStackTrace();
			MvLogger.log(Level.SEVERE, "Something went wrong");
		}
		
	}

	private final static Method method;

	static {
		Method stub = null;
		try {
			stub = Class.forName("me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI").getDeclaredMethod("resolve_sign", Block.class, boolean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		method = stub;
	}

	private void formatPastedSchematic(World world, CuboidRegion region, boolean a) {

		for(BlockVector3 vector : region) {
	    	Block block = world.getBlockAt(vector.getX(), vector.getY(), vector.getZ());
			try {
				method.invoke(null, block, a);
			} catch(Exception ex) {
				ex.printStackTrace();
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

}
