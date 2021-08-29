package me.DenBeKKer.ntdLuckyBlock.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Material;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;

public class CraftTheory {
	
	public static Collection<LuckyRecipe> getDefault(LuckyBlockType type) {
		
		Collection<LuckyRecipe> collection = new ArrayList<>();
		
		collection.add(new LuckyRecipe(type, new LuckyRecipeItem[] {
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(type.asDye()),
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(Material.GOLD_INGOT),
				new LuckyRecipeItem(Material.GOLD_INGOT)
		}, "luckyblock.craft." + type.name().toLowerCase(), false));
		
		collection.add(new LuckyRecipe(type, new LuckyRecipeItem[] {
				new LuckyRecipeItem(type.asDye()),
				new LuckyRecipeItem(type).allowAnyLuckyBlock()
		}, "luckyblock.dye." + type.name().toLowerCase(), true));
		return collection;
		
	}
	
	public static Collection<LuckyRecipe> getFromConfig(LuckyBlockType type) {
		LBMain.log(Level.WARNING, "Custom crafts feature available only for premium version");
		LBMain.log(Level.WARNING, "Check out premium plugin version - https://www.spigotmc.org/resources/94872");
		return new ArrayList<>();
	}
	
}
