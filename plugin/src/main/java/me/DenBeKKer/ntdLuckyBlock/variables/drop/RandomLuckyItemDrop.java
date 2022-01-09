package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.events.ItemSpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RandomLuckyItemDrop implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private final int a;
	
	/**
	 * 
	 * @param a - Amount
	 */
	public RandomLuckyItemDrop(int a) {
		this.a = a;
	}

	public int getAmount() { return a; }

	@Override
	public void execute(LuckyBlockType related, Block b, Player target) {

		if(LuckyBlockType.map().size() == 0) return;
		ItemStack stack = LuckyBlockType.map().get(LuckyBlockType.random(true)).getSkull();
		stack.setAmount(a);
		Item item = b.getWorld().dropItem(b.getLocation().add(.5, .4, .5), stack);
		Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(related, item, target));

	}
	
}
