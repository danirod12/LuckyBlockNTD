package me.DenBeKKer.ntdLuckyBlock.variables.gui;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.PlayerHead;
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

    public CountGui(Player p, int start, int limit, int offset, ConfirmEvent event, ItemStack bag, boolean eco, double price_per_chart) {

        this.start = current = start;
        this.limit = limit;
        this.offset = offset;
        this.event = event;
        this.bag = bag.clone();
        this.eco = eco;
        this.price = price_per_chart;
        this.inventory = Bukkit.createInventory(p, 27, Message.GUI_COUNT_TITLE.getAsString());
        inventory.setItem(18, PlayerHead.PREVIOUS_WOOD.getHead(Message.GUI_COUNT_BACK.getAsString(), null));
        inventory.setItem(26, PlayerHead.NEXT_WOOD.getHead(Message.GUI_COUNT_CONFIRM.getAsString(), null));
        generateInventory();

        Bukkit.getPluginManager().registerEvents(this, LBMain.getInstance());
        p.openInventory(inventory);

    }

    private void generateInventory() {

        if (current > offset)
            inventory.setItem(11, PlayerHead.MINUS_WOOD.getHead(Message.GUI_COUNT_REMOVE.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        else
            inventory.setItem(11, PlayerHead.MINUS_STONE.getHead(Message.GUI_COUNT_REMOVE.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        if (current < limit)
            inventory.setItem(15, PlayerHead.PLUS_WOOD.getHead(Message.GUI_COUNT_ADD.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));
        else
            inventory.setItem(15, PlayerHead.PLUS_STONE.getHead(Message.GUI_COUNT_ADD.getAsString()
                    .replace("%offset%", String.valueOf(offset)), null));

        generateBag();
        inventory.setItem(13, bag);

    }

    private void generateBag() {

        ItemMeta meta = bag.getItemMeta();
//		meta.setLore(Arrays.asList("\u00a7f ", "\u00a7fYou will get: \u00a76" + current, (eco ? ("\u00a7fYou will give: \u00a7c" + getPrice()) : "")));
        meta.setLore(Arrays.asList("\u00a7f ", Message.GUI_COUNT_GET.getAsString().replace("%amount%", String.valueOf(current)),
                eco ? (Message.GUI_COUNT_GIVE.getAsString().replace("%price%", String.valueOf(getPrice()))) : ""));
        bag.setItemMeta(meta);

    }

    private String getPrice() {
        return LBMain.getInstance().getEconomyBridge().format((int) (current * price));
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) event.onConfirm(-54);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {

        if (e.getSlot() < 0) return;
        if (e.getClickedInventory().equals(inventory)) {

            e.setCancelled(true);
            ItemStack item = e.getInventory().getItem(e.getSlot());
            if (item == null || !LBMain.getInstance().materialFactory.isSkull(item)) return;

            switch (e.getSlot()) {
                case 18: {
                    if (LBMain.isDebug()) LBMain.debug("back");
                    event.goBack();
                    return;
                }
                case 26: {
                    if (LBMain.isDebug()) LBMain.debug("confirm");
                    event.onConfirm(current);
                    return;
                }
                case 11: {
                    if (LBMain.isDebug()) LBMain.debug("minus");
                    current -= offset;
                    if (current < start) current = start;
                    generateBag();
                    generateInventory();
                    return;
                }
                case 15: {
                    if (LBMain.isDebug()) LBMain.debug("plus");
                    current += offset;
                    if (current > limit) current = limit;
                    generateBag();
                    generateInventory();
                }
            }

        }

    }

}
