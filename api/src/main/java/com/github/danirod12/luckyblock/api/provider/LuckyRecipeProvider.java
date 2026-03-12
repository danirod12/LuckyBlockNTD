package com.github.danirod12.luckyblock.api.provider;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipe;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipeItem;
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
