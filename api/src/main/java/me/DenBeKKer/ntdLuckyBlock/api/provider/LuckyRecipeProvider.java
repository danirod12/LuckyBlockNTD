package me.DenBeKKer.ntdLuckyBlock.api.provider;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipeItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface LuckyRecipeProvider {

    ILuckyRecipeItem createItem(Material material);

    ILuckyRecipeItem createItem(DyeColor color);

    ILuckyRecipeItem createItem(LuckyBlockKey type);

    ILuckyRecipeItem createItem(ItemStack itemStack);

    ILuckyRecipeItem createItemAnyLuckyBlockType();

    ILuckyRecipe createRecipe(LuckyBlockKey related, ILuckyRecipeItem[] items, String permission, boolean anyMatrix);

    List<ILuckyRecipe> getDefaultCrafts(LuckyBlockKey key);

    List<ILuckyRecipe> getCustomCrafts(LuckyBlockKey type);
}
