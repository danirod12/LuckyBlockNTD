package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class ItemDrop implements LuckyDrop {
	
	@SerializedName(value = "item")
	private ItemStack item;
	
	/**
	 * @param item - ItemStack will be dropped
	 */
	public ItemDrop(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public void execute(Block b, Player target) { execute(b); }
	
	@Override
	public void execute(Block b) {
		b.getWorld().dropItem(b.getLocation().add(0.5, 0.4, 0.5), item);
	}
	
}
