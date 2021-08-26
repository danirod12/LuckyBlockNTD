package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import java.io.File;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.sk89q.LBWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class SchematicDrop implements LuckyDrop {
	
	@SerializedName(value = "block")
	private boolean b;
	@SerializedName(value = "file")
	private File file;
	
	public SchematicDrop(File file, boolean b) {
		this.b = b;
		this.file = file;
	}
	
	@Override
	public void execute(Block b, Player target) {
		LBWorldEdit.paste(file, this.b ? b : target.getLocation().getBlock());
	}
	
	@Override
	public void execute(Block b) { if(this.b) LBWorldEdit.paste(file, b); }
	
}
