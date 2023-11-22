package me.DenBeKKer.ntdLuckyBlock.loader;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class ConvertManager implements Listener {

    private final Map<Config, Set<String>> map = new HashMap<>();
    private boolean verifyUUID, verifyTAG;
    private boolean factory;

    public static void convert(Player player) {

        LBMain.debug("Converting " + player.getName() + "...");
        final PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {

            ItemStack stack = inventory.getItem(slot);
            if (stack == null) continue;

            LuckyBlockType type = LuckyBlockAPI.parseOldLuckyBlock(stack);
            if (type == null) continue;

            int amount = stack.getAmount();
            stack = type.isLoaded() ? LuckyBlockType.map().get(type).getSkull() : null;
            if (stack != null) stack.setAmount(amount);
            inventory.setItem(slot, stack);
            LBMain.debug("Converted (" + type.name() + ", " + amount + ") for " + player.getName());

        }

    }

    public void add(Config loaded, String path) {
        map.computeIfAbsent(loaded, n -> new HashSet<>()).add(path);
    }

    public int getRequests() {
        return map.values().stream().mapToInt(Collection::size).sum();
    }

    public Map<Config, Collection<String>> getRequestMap() {
        return new HashMap<>(map);
    }

    public void resetIndexes() {
        this.map.clear();
    }

    public boolean isVerifyUUID() {
        return verifyUUID;
    }

    public boolean isVerifyTAG() {
        return verifyTAG;
    }

    public void setup(boolean verifyUUID, boolean verifyTAG) {
        this.verifyUUID = verifyUUID;
        this.verifyTAG = verifyTAG;
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (factory) convert(e.getPlayer());
    }

    @EventHandler
    public void inventory(InventoryClickEvent e) {

        if (!factory) return;

        if (e.getInventory().getType() != InventoryType.PLAYER) {

            if (e.getCurrentItem() != null) {

                LuckyBlockType type = LuckyBlockAPI.parseOldLuckyBlock(e.getCurrentItem());
                if (type != null) {

                    int amount = e.getCurrentItem().getAmount();
                    ItemStack stack = type.isLoaded() ? LuckyBlockType.map().get(type).getSkull() : null;
                    if (stack != null) stack.setAmount(amount);
                    e.setCurrentItem(stack);
                    LBMain.debug("Converted (" + type.name() + ", " + amount + ") for " + e.getWhoClicked().getName());

                }

            }

        }

    }

    public boolean isFactoryEnabled() {
        return factory;
    }

    public void setFactoryEnabled(boolean factory) {
        this.factory = factory;
    }

}
