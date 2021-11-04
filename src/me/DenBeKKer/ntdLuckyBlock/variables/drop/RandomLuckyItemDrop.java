package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class RandomLuckyItemDrop implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private int a;
	
	/**
	 * 
	 * @param a - Amount
	 */
	public RandomLuckyItemDrop(int a) {
		this.a = a;
	}
	
	@Override
	public void execute(Block b, Player target) { execute(b); }
	
	@Override
	public void execute(Block b) {
		if(LuckyBlockType.map().size() == 0) return;
		ItemStack stack = LuckyBlockType.map().get(LuckyBlockType.random(true)).getSkull();
		stack.setAmount(a);
		b.getWorld().dropItem(b.getLocation().add(.5, .4, .5), stack);
	}
	
}
