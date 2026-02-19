package me.DenBeKKer.ntdLuckyBlock.recipe;

import me.DenBeKKer.ntdLuckyBlock.api.event.PrepareLuckyBlockCraftEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    private final LuckyBlockEngine luckyBlockEngine;

    public CraftListener(LuckyBlockEngine luckyBlockEngine) {
        this.luckyBlockEngine = luckyBlockEngine;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        Player player;
        try {
            player = (Player) event.getViewers().get(0);
        } catch (Exception ignored) {
            return;
        }

        for (LuckyBlock block : luckyBlockEngine.getLoaded()) {
            for (ILuckyRecipe recipe : block.getRecipes()) {
                if (recipe == null || (luckyBlockEngine.getConfigHolder().craftPermissions
                        && !recipe.hasAccess(player))) {
                    continue;
                }
                int verification = recipe.verify(inventory.getMatrix());
                if (verification > 0) {
                    PrepareLuckyBlockCraftEvent customEvent = new PrepareLuckyBlockCraftEvent(player,
                            inventory.getMatrix(), verification, recipe);
                    Bukkit.getPluginManager().callEvent(customEvent);
                    if (customEvent.isCancelled()) {
                        return;
                    }
                    inventory.setResult(block.getItem());
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftItem(InventoryClickEvent event) {
        if (event.getInventory() instanceof CraftingInventory && event.getSlot() == 0) {
            CraftingInventory craftingInventory = (CraftingInventory) event.getInventory();
            HumanEntity human = event.getWhoClicked();
            if (craftingInventory.getResult() != null) {
                this.luckyBlockEngine.parseLuckyBlock(craftingInventory.getResult()).ifPresent(type -> {
                    event.setCancelled(true);
                    this.luckyBlockEngine.get(type).ifPresent(block -> {
                        for (ILuckyRecipe recipe : block.getRecipes()) {
                            int amount = recipe.verify(craftingInventory.getMatrix());
                            int maxAmount = amount;
                            if (amount > 0 && !event.getClick().isShiftClick()) {
                                amount = 1;
                            }
                            if (amount > 0) {
                                ItemStack stack = block.getItem(amount);
                                if (event.getClick().isShiftClick()) {
                                    for (ItemStack value : human.getInventory().addItem(stack).values()) {
                                        human.getWorld().dropItem(human.getLocation(), value);
                                    }
                                } else {
                                    ItemStack current = human.getItemOnCursor();
                                    if (current != null && !current.getType().name().contains("AIR")) {
                                        if (current.isSimilar(stack)) {
                                            current.setAmount(current.getAmount() + stack.getAmount());
                                        } else {
                                            // not craft
                                            return;
                                        }
                                    } else {
                                        human.setItemOnCursor(stack);
                                    }
                                }

                                ItemStack[] itemStacks = craftingInventory.getMatrix();
                                for (int i = 0; i < itemStacks.length; i++) {
                                    if (itemStacks[i] == null || itemStacks[i].getAmount() <= amount) {
                                        itemStacks[i] = null;
                                    } else {
                                        itemStacks[i].setAmount(itemStacks[i].getAmount() - amount);
                                    }
                                }
                                craftingInventory.setMatrix(itemStacks);
                                if (maxAmount == amount) {
                                    craftingInventory.setResult(null);
                                }
                                return;
                            }
                        }
                        // error / runtime recipe change
                        human.closeInventory();
                    });
                });
            }
        }
    }
}
