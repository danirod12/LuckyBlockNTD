package me.DenBeKKer.ntdLuckyBlock.recipe;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;

@SuppressWarnings("deprecation")
public class LuckyRecipeItem {
	
	private enum Type {
		
		MATERIAL,
		LUCKYBLOCK,
		DYE;
		
	}
	
	private Enum<?> object;
	private Type type;
	
	public LuckyRecipeItem(Material material) {
		object = material;
		type = Type.MATERIAL;
	}
	
	public LuckyRecipeItem(DyeColor dye) {
		object = dye;
		type = Type.DYE;
	}
	
	public LuckyRecipeItem(LuckyBlockType luckyblock) {
		object = luckyblock;
		type = Type.LUCKYBLOCK;
	}
	
	public LuckyRecipeItem allowAnyLuckyBlock() {
		if(type == Type.LUCKYBLOCK)
			object = null;
		return this;
	}
	
	public boolean isMatch(ItemStack item) {
		
		if(item == null) return false;
		switch(type) {
		case DYE: {
			
			if(LBMain.getInstance().factory instanceof Mat1_12) {
				if(!(item.getData() instanceof Dye)) return false;
				return ((Dye) item.getData()).getColor() == (DyeColor) object;
			} else {
				return item.getType().name().equalsIgnoreCase(object.name() + "_DYE");
			}
			
		}
		case MATERIAL: return item.getType() == (Material) object;
		case LUCKYBLOCK: {
			
			if(object == null) {
				return LuckyBlockAPI.isLuckyBlock(item);
			} else return LuckyBlockAPI.getLuckyBlock(item) == (LuckyBlockType) object;
			
		}
		}
		return false;
		
	}
	
	@Override
	public String toString() { return "{\"type\":\"" + type.name() + "\",\"option\":\"" + (object == null ? "null" : object.name()) + "\"}"; }
	
}
