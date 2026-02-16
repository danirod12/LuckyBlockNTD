package me.DenBeKKer.ntdLuckyBlock.api.event;

import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PrepareLuckyBlockCraftEvent extends CancellableEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack[] matrix;
    private final ILuckyRecipe recipe;

    public PrepareLuckyBlockCraftEvent(Player player, ItemStack[] matrix, ILuckyRecipe recipe) {
        this.player = player;
        this.matrix = matrix;
        this.recipe = recipe;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack[] getMatrix() {
        return matrix;
    }

    public ILuckyRecipe getLuckyRecipe() {
        return recipe;
    }

}
