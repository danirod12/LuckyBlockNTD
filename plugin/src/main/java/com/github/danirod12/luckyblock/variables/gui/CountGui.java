package com.github.danirod12.luckyblock.variables.gui;

import com.cryptomorin.xseries.XMaterial;
import com.github.danirod12.luckyblock.LBMain;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import com.github.danirod12.luckyblock.variables.PlayerHead;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CountGui implements Listener {
    private final int start;
    private final int limit;
    private final int offset;
    private final ConfirmEvent event;

    private final Inventory inventory;
    private final ItemStack bag;

    private final boolean eco;
    private final double price;
    private int current;

    public CountGui(Player player, int start, int limit, int offset,
                    ConfirmEvent event, ItemStack bag,
                    boolean eco, double pricePerChart) {
        this.start = current = start;
        this.limit = limit;
        this.offset = offset;
        this.event = event;
        this.bag = bag.clone();
        this.eco = eco;
        this.price = pricePerChart;
        this.inventory = Bukkit.createInventory(player, 27, Message.GUI_COUNT_TITLE.getAsString());
        inventory.setItem(18, PlayerHead.PREVIOUS_WOOD.getHead(Message.GUI_COUNT_BACK.getAsString(), null));
        inventory.setItem(26, PlayerHead.NEXT_WOOD.getHead(Message.GUI_COUNT_CONFIRM.getAsString(), null));
        generateInventory();

        Bukkit.getPluginManager().registerEvents(this, LuckyBlockAPI.getInstance());
        player.openInventory(inventory);
    }

    private void generateInventory() {
        if (current > offset) {
            inventory.setItem(11, PlayerHead.MINUS_WOOD.getHead(Message.GUI_COUNT_REMOVE.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        } else {
            inventory.setItem(11, PlayerHead.MINUS_STONE.getHead(Message.GUI_COUNT_REMOVE.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        }
        if (current < limit) {
            inventory.setItem(15, PlayerHead.PLUS_WOOD.getHead(Message.GUI_COUNT_ADD.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        } else {
            inventory.setItem(15, PlayerHead.PLUS_STONE.getHead(Message.GUI_COUNT_ADD.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        }

        generateBag();
        inventory.setItem(13, bag);
    }

    private void generateBag() {
        ItemMeta meta = bag.getItemMeta();
        meta.setLore(Arrays.asList(
                "§f ", Message.GUI_COUNT_GET.getAsString().replace("%amount%", String.valueOf(current)),
                eco ? (Message.GUI_COUNT_GIVE.getAsString().replace("%price%", String.valueOf(getPrice()))) : ""
        ));
        bag.setItemMeta(meta);
    }

    private String getPrice() {
        return ((LBMain) LuckyBlockAPI.getInstance()).getEconomyBridge().format((int) (current * price));
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            this.event.onConfirm(-54);
        }
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getSlot() < 0) {
            return;
        }
        if (event.getClickedInventory().equals(inventory)) {

            event.setCancelled(true);
            ItemStack item = event.getInventory().getItem(event.getSlot());
            if (item == null || XMaterial.matchXMaterial(item) != XMaterial.PLAYER_HEAD) {
                return;
            }

            switch (event.getSlot()) {
                case 18: {
                    this.event.goBack();
                    return;
                }
                case 26: {
                    this.event.onConfirm(current);
                    return;
                }
                case 11: {
                    current -= offset;
                    if (current < start) {
                        current = start;
                    }
                    generateBag();
                    generateInventory();
                    return;
                }
                case 15: {
                    current += offset;
                    if (current > limit) {
                        current = limit;
                    }
                    generateBag();
                    generateInventory();
                }
            }
        }
    }
}
