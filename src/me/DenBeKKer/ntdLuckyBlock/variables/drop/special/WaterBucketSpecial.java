package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class WaterBucketSpecial implements LuckyDrop {
	
	@SerializedName(value = "height")
	private int h;
	
	public WaterBucketSpecial(int h) {
		this.h = h;
	}
	
	@Override
	public void execute(Block b, Player target) {
		target.sendMessage(Message.WATER_BUCKET.getAsString());
		target.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
		target.teleport(target.getLocation().add(0, h, 0));
	}
	
	@Override
	public void execute(Block b) { return; }
	
}
