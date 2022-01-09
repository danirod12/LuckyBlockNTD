package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.loader.CustomSaver;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

public class SchematicDrop implements LuckyDrop, CustomSaver {
	
	@SerializedName(value = "block")
	private final boolean b;
	@SerializedName(value = "file")
	private final File file;

	public File getFile() {
		return file;
	}

	public boolean atBlock() {
		return b;
	}

	/**
	 * 
	 * @param file - Schematic file
	 * @param b - Place at block (true); Place at player (false)
	 */
	public SchematicDrop(File file, boolean b) {
		this.b = b;
		this.file = file;
	}

	@Override
	public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
		LBWorldEdit.paste(file, this.b || target == null ? b : target.getLocation().getBlock());
	}
	
	@Override
	public String getDescription() {
		return file.getName() + " : " + (b ? "true" : "false");
	}
	
	public static LuckyDrop load(String description) {
		
		if(!LBMain.getIsSk89q()) {
			LBMain.getInstance().getLogger().log(Level.WARNING, "WorldEdit or WorldGuard was not found, lucky item \""
					+ description + "\" wont be loaded");
			throw new UnsupportedOperationException();
		}
		
		boolean b = false;
		try {
			b = Boolean.valueOf(description.split(" : ")[1]);
		} catch(Exception ex) {
			throw new UnsupportedOperationException();
		}
		
		String schematic = description.split(" : ")[0];
		String file$name = schematic.endsWith(".schem") ? schematic : schematic + ".schem";
		
		File file = new File(LBMain.getSchematicsFolder(), file$name);
		if(!file.exists()) {
			
			file$name = schematic.endsWith(".schematic") ? schematic : schematic + ".schematic";
			file = new File(LBMain.getSchematicsFolder(), file$name);
			
			if(!file.exists()) {
				LBMain.getInstance().getLogger().log(Level.WARNING, "Schematic " + file.getPath() + " not found");
				return null;
			}
			
		}
		
		return new SchematicDrop(file, b);
		
	}
	
}
