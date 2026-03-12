package me.DenBeKKer.ntdLuckyBlock.api.event;

import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PrepareLuckyBlockCraftEvent extends CancellableEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ItemStack[] matrix;
    private final int amount;
    private final ILuckyRecipe recipe;

    public PrepareLuckyBlockCraftEvent(Player player, ItemStack[] matrix, int amount, ILuckyRecipe recipe) {
        this.player = player;
        this.matrix = matrix;
        this.amount = amount;
        this.recipe = recipe;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
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

    public int getAmount() {
        return amount;
    }
}
