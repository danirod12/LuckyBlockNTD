package com.github.danirod12.luckyblock.api.provider;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipe;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipeItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Provides methods for creating and managing recipes for Lucky Blocks.
 */
public interface LuckyRecipeProvider {

    /**
     * Creates a new recipe item based on the given material.
     *
     * @param material the material to create the recipe item from
     * @return a new instance of ILuckyRecipeItem representing the given material
     */
    ILuckyRecipeItem createItem(Material material);

    /**
     * Creates a new recipe item based on the given dye color.
     *
     * @param color the dye color to create the recipe item from
     * @return a new instance of ILuckyRecipeItem representing the given dye color
     */
    ILuckyRecipeItem createItem(DyeColor color);

    /**
     * Creates a new recipe item based on the given Lucky Block type.
     *
     * @param type the Lucky Block type to create the recipe item from
     * @return a new instance of ILuckyRecipeItem representing the given Lucky Block type
     */
    ILuckyRecipeItem createItem(LuckyBlockKey type);

    /**
     * Creates a new recipe item based on the given ItemStack.
     *
     * @param itemStack the ItemStack to create the recipe item from
     * @return a new instance of ILuckyRecipeItem representing the given ItemStack
     */
    ILuckyRecipeItem createItem(ItemStack itemStack);

    /**
     * Creates a new recipe item that can represent any type of Lucky Block.
     *
     * @return a new instance of ILuckyRecipeItem that can represent any type of Lucky Block
     */
    ILuckyRecipeItem createItemAnyLuckyBlockType();

    /**
     * Creates a new recipe for a Lucky Block with the specified
     * related type, items, permission, and matrix requirement.
     *
     * @param related    the related Lucky Block type for this recipe
     * @param items      the array of recipe items required for this recipe
     * @param permission the permission required to use this recipe (can be null or empty for no permission)
     * @param anyMatrix  whether this recipe can be crafted in any crafting matrix (true)
     *                   or only in a provided 3x3 crafting grid with correct order (false)
     * @return a new instance of ILuckyRecipe representing the created recipe
     */
    ILuckyRecipe createRecipe(LuckyBlockKey related, ILuckyRecipeItem[] items, String permission, boolean anyMatrix);

    /**
     * Retrieves the default recipes for a given Lucky Block type.
     *
     * @param key the Lucky Block type for which to retrieve the default recipes
     * @return a list of ILuckyRecipe instances representing the default recipes for the specified Lucky Block type
     */
    List<ILuckyRecipe> getDefaultCrafts(LuckyBlockKey key);

    /**
     * Retrieves the custom recipes for a given Lucky Block type.
     *
     * @param type the Lucky Block type for which to retrieve the custom recipes
     * @return a list of ILuckyRecipe instances representing the custom recipes for the specified Lucky Block type
     */
    List<ILuckyRecipe> getCustomCrafts(LuckyBlockKey type);
}
