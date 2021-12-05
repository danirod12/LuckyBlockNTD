package me.DenBeKKer.ntdLuckyBlock.util.material;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.util.ColorData;

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
		BLACK_PANE,
		WHITE_WOOL,
		BEEF;
		
	}
	
	public ItemStack getItem(Mat mat, int amount);
	
	public ItemStack getGlass(ColorData color, int amount);
	
	public boolean isSkull(ItemStack item);

	public String build();
	
	public boolean isOakSign(Material type);
	
	public boolean isSkull(Material type);
	
	public ItemStack getItemInMainHand(Player player);
	
	public static void setData(Block block, byte data) {
		try {
			block.getClass().getDeclaredMethod("setData", byte.class).invoke(block, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final static List<Material> WOOLS = Stream.of(Material.values())
			.filter(n -> !n.name().startsWith("LEGACY_") && n.name().contains("WOOL"))
			.collect(Collectors.toList());
	
}
