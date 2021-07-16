package me.DenBeKKer.ntdLuckyBlock.util.material;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Mat1_13 implements IMat {
	
	@Override
	public ItemStack getItem(Mat mat, int i) {
		switch(mat) {
		
			case PLAYER_SKULL: return new ItemStack(Material.valueOf("PLAYER_HEAD"), i);
			
//			case RED_GLASS: return new ItemStack(Material.valueOf("RED_STAINED_GLASS"), i);
//			case GREEN_GLASS: return new ItemStack(Material.valueOf("GREEN_STAINED_GLASS"), i);
//			case YELLOW_GLASS: return new ItemStack(Material.valueOf("YELLOW_STAINED_GLASS"), i);
//			case BLUE_GLASS: return new ItemStack(Material.valueOf("CYAN_STAINED_GLASS"), i);
//			case BLACK_GLASS: return new ItemStack(Material.valueOf("BLACK_STAINED_GLASS"), i);
			
			case GRAY_PANE: return new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"), i);
			case BLACK_PANE: return new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE"), i);
			
			default: return null;
			
		}
	}
	
	@Override
	public boolean isSkull(ItemStack item) { return item.getType() == Material.valueOf("PLAYER_HEAD"); }
	
	@Override
	public String build() { return "main"; }
	
	@Override
	public ItemStack getGlass(DyeColor color, int amount) {
		return new ItemStack(Material.valueOf(color.name() + "_STAINED_GLASS"), amount);
	}
	
}
