package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.recipe.LuckyRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PrepareLuckyBlockCraftEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack[] matrix;
    private final LuckyRecipe recipe;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    private boolean c = false;

    @Override
    public void setCancelled(boolean cancel) {
        c = cancel;
    }

    public PrepareLuckyBlockCraftEvent(Player player, ItemStack[] matrix, LuckyRecipe recipe) {
        this.player = player;
        this.matrix = matrix;
        this.recipe = recipe;
    }

    public Player getPlayer() { return player; }
    public ItemStack[] getMatrix() { return matrix; }
    public LuckyRecipe getLuckyRecipe() { return recipe; }

}
