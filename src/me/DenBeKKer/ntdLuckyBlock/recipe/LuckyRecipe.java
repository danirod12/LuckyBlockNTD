package me.DenBeKKer.ntdLuckyBlock.recipe;

import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;

public class LuckyRecipe {
	
	private LuckyRecipeItem[] items;
	private LuckyBlockType type;
	private String permission;
	private boolean any_matrix = false;
	
	public LuckyRecipe(LuckyBlockType type, LuckyRecipeItem[] items, boolean anymatrix) {
		
		if(!anymatrix && items.length != 9) throw new IllegalArgumentException("Please, provide 9 LuckyRecipeItems");
		
		if(type.get() == null) throw new UnsupportedOperationException("LuckyBlockType " + type.name() + " is unloaded");
		
		this.items = items;
		this.type = type;
		this.any_matrix = anymatrix;
		
	}
	
	public LuckyRecipe(LuckyBlockType type, LuckyRecipeItem[] items, String permission, boolean anymatrix) {
		this(type, items, anymatrix);
		this.permission = permission;
	}
	
	public boolean verify(ItemStack[] origin) {
		
		boolean debug = LBMain.isDebug();
		
		if(origin.length != 9) {
			if(debug) LBMain.debug("Provided " + origin.length + " items (Need 9)");
			return false;
		}
		
		if(any_matrix) return verifyAny(origin);
		
		for(int i = 0; i < origin.length; i++) {
			if(items[i] == null) {
				if(origin[i] == null) continue;
				return false;
			}
			if(!items[i].isMatch(origin[i])) {
				if(debug) LBMain.debug("Item " + items[i].toString() + " not matchs " + (origin[i] == null ? "null" : origin[i].getType().name()));
				return false;
			}
		}
		
		return true;
		
	}
	
	public boolean verifyAny(ItemStack[] origin) {
		
		if(origin.length != 9) return false;
		
		ItemStack[] array = origin.clone();
		
		items:
		for(LuckyRecipeItem item : items) {
			
			if(item == null) continue items;
			for(int i = 0; i < array.length; i++) {
				
				if(item.isMatch(array[i])) {
					array[i] = null;
					continue items;
				}
				
			}
			return false;
			
		}
		return Stream.of(array).filter(n -> n != null).count() == 0;
		
	}
	
	public ItemStack getResult() { return type.get().getSkull(); }
	
	public boolean hasAccess(Player player) {
		if(permission == null || player == null) return true;
		return player.hasPermission(permission);
	}
	
}
