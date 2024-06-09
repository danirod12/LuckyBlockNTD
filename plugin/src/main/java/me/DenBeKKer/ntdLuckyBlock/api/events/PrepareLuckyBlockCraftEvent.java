package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.recipe.LuckyRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PrepareLuckyBlockCraftEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack[] matrix;
    private final int amount;
    private final LuckyRecipe recipe;
    private boolean c = false;

    @Deprecated
    public PrepareLuckyBlockCraftEvent(Player player, ItemStack[] matrix, LuckyRecipe recipe) {
        this(player, matrix, 1, recipe);
    }

    public PrepareLuckyBlockCraftEvent(Player player, ItemStack[] matrix, int amount, LuckyRecipe recipe) {
        this.player = player;
        this.matrix = matrix;
        this.amount = amount;
        this.recipe = recipe;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    @Override
    public void setCancelled(boolean cancel) {
        c = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack[] getMatrix() {
        return matrix;
    }

    public LuckyRecipe getLuckyRecipe() {
        return recipe;
    }

    public int getAmount() {
        return amount;
    }
}
