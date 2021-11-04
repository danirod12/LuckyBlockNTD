package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class LuckyItemDrop implements LuckyDrop {
	
	@SerializedName(value = "type")
	private LuckyBlockType item;
	@SerializedName(value = "amount")
	private int amount;
	
	/**
	 * 
	 * @param type - LuckyBlockType will be dropped
	 * @param amount - LuckyBlock's amount
	 */
	public LuckyItemDrop(LuckyBlockType type, int amount) {
		this.item = type;
		this.amount = amount;
	}
	
	@Override
	public void execute(Block b, Player target) { execute(b); }
	
	@Override
	public void execute(Block b) {
		if(item.isLoaded()) {
			ItemStack item = LuckyBlockType.map().get(this.item).getSkull();
			item.setAmount(amount);
			b.getWorld().dropItem(b.getLocation().add(.5, .4, .5), item);
		}
	}
	
}
