package me.DenBeKKer.ntdLuckyBlock.recipe;

import me.DenBeKKer.ntdLuckyBlock.api.event.PrepareLuckyBlockCraftEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

// TODO check
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
                if (recipe.verify(inventory.getMatrix()) > 0) {
                    PrepareLuckyBlockCraftEvent customEvent = new PrepareLuckyBlockCraftEvent(player,
                            inventory.getMatrix(), recipe);
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

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        luckyBlockEngine.parseLuckyBlock(event.getRecipe().getResult()).ifPresent(type -> {
            event.setCancelled(true);
            luckyBlockEngine.get(type).ifPresent(block -> {
                CraftingInventory inventory = event.getInventory();
                for (ILuckyRecipe recipe : block.getRecipes()) {
                    int amount = recipe.verify(inventory.getMatrix());
                    if (amount > 0 && !event.getClick().isShiftClick())
                        amount = 1;
                    if (amount > 0) {
                        inventory.setResult(null);
                        ItemStack[] itemStacks = inventory.getMatrix();
                        for (int i = 0; i < itemStacks.length; i++) {
                            if (itemStacks[i] == null || itemStacks[i].getAmount() <= amount) {
                                itemStacks[i] = null;
                            } else {
                                itemStacks[i].setAmount(itemStacks[i].getAmount() - 1);
                            }
                        }
                        inventory.setMatrix(itemStacks);
                        ItemStack stack = block.getItem(amount);
                        if (event.getClick().isShiftClick()) {
                            Map<Integer, ItemStack> map = event.getWhoClicked().getInventory().addItem(stack);
                            if (map.size() != 0) {
                                for (ItemStack value : map.values()) {
                                    event.getWhoClicked().getWorld()
                                            .dropItem(event.getWhoClicked().getLocation(), value);
                                }
                            }
                            return;
                        }
                        ItemStack current = event.getWhoClicked().getItemOnCursor();
                        if (current != null && !current.getType().name().contains("AIR") && current.isSimilar(stack)) {
                            current.setAmount(current.getAmount() + stack.getAmount());
                            event.getWhoClicked().setItemOnCursor(current);
                            return;
                        }
                        event.getWhoClicked().setItemOnCursor(stack);
                        return;
                    }
                }
                event.getWhoClicked().closeInventory();
            });
        });
    }
}
