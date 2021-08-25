package me.DenBeKKer.ntdLuckyBlock.util.material;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface IMat {
	
	public enum Mat {
		
		PLAYER_SKULL,
		
//		YELLOW_GLASS,
//		GREEN_GLASS,
//		RED_GLASS,
//		BLUE_GLASS,
//		BLACK_GLASS,
//		
		GRAY_PANE,
		BLACK_PANE;
		
	}
	
	public ItemStack getItem(Mat mat, int amount);
	
	public ItemStack getGlass(DyeColor color, int amount);
	
	public boolean isSkull(ItemStack item);

	public String build();
	
	public boolean isOakSign(Material type);
	
}
