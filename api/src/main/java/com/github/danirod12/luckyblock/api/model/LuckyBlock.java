package com.github.danirod12.luckyblock.api.model;

import com.github.danirod12.luckyblock.api.setup.AnimationSetup;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipe;
import com.github.danirod12.luckyblock.api.setup.ShopSetup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a Lucky Block, which can be placed in the world and interacted with by players.
 * It contains information about the block's item representation, icon, recipes, shop setup,
 * animation setup, and custom name.
 */
public interface LuckyBlock {

    /**
     * Sets the item representation of the Lucky Block.
     *
     * @param itemStack The ItemStack to set as the item representation.
     */
    void setItem(ItemStack itemStack);

    /**
     * Sets the icon representation of the Lucky Block.
     *
     * @param itemStack The ItemStack to set as the icon representation.
     */
    void setIcon(ItemStack itemStack);

    /**
     * Sets both the item and icon representations of the Lucky Block to the same ItemStack.
     *
     * @param itemStack The ItemStack to set as both the item and icon representation.
     */
    default void setItemAndIcon(ItemStack itemStack) {
        setItem(itemStack);
        setIcon(itemStack);
    }

    /**
     * Removes recipes from the Lucky Block that match the given predicate.
     *
     * @param predicate The predicate to determine which recipes to remove.
     */
    void removeRecipes(Predicate<ILuckyRecipe> predicate);

    /**
     * Adds the given recipes to the Lucky Block.
     *
     * @param recipes The recipes to add.
     */
    void addRecipes(ILuckyRecipe... recipes);

    /**
     * Sets the shop setup for the Lucky Block.
     *
     * @param shopSetup The ShopSetup to set for the Lucky Block.
     */
    void setShopSetup(ShopSetup shopSetup);

    /**
     * Sets the animation setup for the Lucky Block.
     *
     * @param animationSetup The AnimationSetup to set for the Lucky Block.
     */
    void setAnimationSetup(AnimationSetup animationSetup);

    /**
     * Sets the custom name for the Lucky Block.
     *
     * @param customName The custom name to set for the Lucky Block.
     */
    void setCustomName(String customName);

    /**
     * Gets the unique key associated with this Lucky Block.
     *
     * @return The LuckyBlockKey representing the unique key of this Lucky Block.
     */
    LuckyBlockKey getKey();

    /**
     * Gets the unique identifier associated with this Lucky Block.
     *
     * @return The Identifier representing the unique identifier of this Lucky Block.
     */
    Identifier getIdentifier();

    /**
     * Gives the item representation of the Lucky Block to the specified player with a default amount of 1.
     *
     * @param player The player to give the item to.
     */
    default void giveItem(Player player) {
        this.giveItem(player, 1);
    }

    /**
     * Gives the item representation of the Lucky Block to the specified player with the specified amount.
     *
     * @param player The player to give the item to.
     * @param amount The amount of the item to give.
     */
    void giveItem(Player player, int amount);

    /**
     * Gets the item representation of the Lucky Block.
     *
     * @return The ItemStack representing the item representation of the Lucky Block.
     */
    ItemStack getItem();

    /**
     * Gets the item representation of the Lucky Block with the specified amount.
     *
     * @param amount The amount to set for the item representation.
     * @return The ItemStack representing the item representation of the Lucky Block with the specified amount.
     */
    default ItemStack getItem(int amount) {
        ItemStack itemStack = getItem();
        if (itemStack == null) {
            return null;
        }
        itemStack.setAmount(amount);
        return itemStack;
    }

    /**
     * Gets the icon representation of the Lucky Block.
     *
     * @return The ItemStack representing the icon representation of the Lucky Block.
     */
    ItemStack getIcon();

    /**
     * Gets the list of recipes associated with the Lucky Block.
     *
     * @return A List of ILuckyRecipe representing the recipes associated with the Lucky Block.
     */
    List<ILuckyRecipe> getRecipes();

    /**
     * Gets the ItemsBag associated with the Lucky Block, which contains the possible drops and their probabilities.
     *
     * @return The ItemsBag representing the possible drops and their probabilities for the Lucky Block.
     */
    ItemsBag getItemsBag();

    /**
     * Sets the ItemsBag for the Lucky Block, which contains the possible drops and their probabilities.
     *
     * @param bag The ItemsBag to set for the Lucky Block.
     */
    void setItemsBag(ItemsBag bag);

    /**
     * Gets the shop setup for the Lucky Block, which defines the items and prices for the in-game shop.
     *
     * @return The ShopSetup representing the shop setup for the Lucky Block.
     */
    ShopSetup getShopSetup();

    /**
     * Gets the animation setup for the Lucky Block, which defines the animations to play when
     * the block is interacted with.
     *
     * @return The AnimationSetup representing the animation setup for the Lucky Block.
     */
    AnimationSetup getAnimationSetup();

    /**
     * Places the Lucky Block in the world at the specified block location.
     *
     * @param block The Block representing the location where the Lucky Block should be placed.
     */
    void placeBlock(Block block);

    /**
     * Gets the custom name of the Lucky Block, which can be displayed to players when they interact with the block.
     *
     * @return The custom name of the Lucky Block.
     */
    String getCustomName();

    /**
     * Plays the opening animation and effects for the Lucky Block when a player interacts with it.
     * <p>
     * Actually open the LuckyBlock using player and a block as source
     *
     * @param instance     The Plugin instance to use for scheduling tasks and managing resources.
     * @param block        The Block representing the location of the Lucky Block being opened.
     * @param target       The Player who is interacting with the Lucky Block.
     * @param dropItems    Whether to drop the items contained in the Lucky Block when it is opened.
     * @param ignoreCancel Whether to ignore any cancellation of the opening event (e.g., by other plugins).
     * @return true if the opening animation and effects were successfully played, false otherwise.
     */
    boolean playOpen(Plugin instance, Block block, Player target, boolean dropItems, boolean ignoreCancel);
}
