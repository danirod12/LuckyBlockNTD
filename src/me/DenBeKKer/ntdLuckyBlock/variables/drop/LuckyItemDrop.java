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
	
	public LuckyItemDrop(LuckyBlockType type, int amount) {
		this.item = type;
		this.amount = amount;
	}
	
	@Override
	public void execute(Block b, Player target) { execute(b); }
	
	@Override
	public void execute(Block b) {
		if(item.get() != null) {
			ItemStack item = this.item.get().getSkull();
			item.setAmount(amount);
			b.getWorld().dropItem(b.getLocation().add(0.5, 0.4, 0.5), item);
			item.setAmount(1);
		}
	}
	
}
