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

public interface LuckyBlock {

    void setItem(ItemStack itemStack);

    void setIcon(ItemStack itemStack);

    default void setItemAndIcon(ItemStack itemStack) {
        setItem(itemStack);
        setIcon(itemStack);
    }

    void removeRecipes(Predicate<ILuckyRecipe> predicate);

    void addRecipes(ILuckyRecipe... recipes);

    void setShopSetup(ShopSetup shopSetup);

    void setAnimationSetup(AnimationSetup animationSetup);

    void setCustomName(String customName);

    LuckyBlockKey getKey();

    Identifier getIdentifier();

    default void giveItem(Player player) {
        this.giveItem(player, 1);
    }

    void giveItem(Player player, int amount);

    ItemStack getItem();

    default ItemStack getItem(int amount) {
        ItemStack itemStack = getItem();
        if (itemStack == null) {
            return null;
        }
        itemStack.setAmount(amount);
        return itemStack;
    }

    ItemStack getIcon();

    List<ILuckyRecipe> getRecipes();

    ItemsBag getItemsBag();

    void setItemsBag(ItemsBag bag);

    ShopSetup getShopSetup();

    AnimationSetup getAnimationSetup();

    void placeBlock(Block block);

    String getCustomName();

    boolean playOpen(Plugin instance, Block block, Player target, boolean dropItems, boolean ignoreCancel);
}
