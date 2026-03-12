package com.github.danirod12.luckyblock.api.setup;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ILuckyRecipe {


    int verify(ItemStack[] origin);

    LuckyBlockKey getType();

    ILuckyRecipeItem[] getItems();

    boolean hasAccess(Player player);
}
