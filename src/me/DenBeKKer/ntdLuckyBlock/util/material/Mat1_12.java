package me.DenBeKKer.ntdLuckyBlock.util.material;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Mat1_12 implements IMat {
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItem(Mat mat, int i) {
		switch(mat) {
		
			case PLAYER_SKULL: return new ItemStack(Material.valueOf("SKULL_ITEM"), i, (short)3);
			
//			case RED_GLASS: return new ItemStack(Material.valueOf("STAINED_GLASS"), i, (short)14);
//			case GREEN_GLASS: return new ItemStack(Material.valueOf("STAINED_GLASS"), i, (short)13);
//			case YELLOW_GLASS: return new ItemStack(Material.valueOf("STAINED_GLASS"), i, (short)4);
//			case BLUE_GLASS: return new ItemStack(Material.valueOf("STAINED_GLASS"), i, (short)9);
//			case BLACK_GLASS: return new ItemStack(Material.valueOf("STAINED_GLASS"), i, (short)15);
//			
			case GRAY_PANE: return new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), i, (short)7);
			case BLACK_PANE: return new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), i, (short)15);
			
			default: return null;
			
		}
	}
	
	@Override
	public boolean isSkull(ItemStack item) { return item.getType() == Material.valueOf("SKULL_ITEM"); }
	
	@Override
	public String build() { return "old"; }
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getGlass(DyeColor color, int amount) {
		return new ItemStack(Material.valueOf("STAINED_GLASS"), amount, (short)color.ordinal());
	}
	
}
