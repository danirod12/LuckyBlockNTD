package me.DenBeKKer.ntdLuckyBlock.util.manager;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.economy.EconomyBridge;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.variables.ConfirmEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.CountGui;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.variables.PlayerHead;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class GuiManager implements Listener {

    private final LBMain instance;
    private final Map<Player, CountGui> map = new HashMap<>();
    private Inventory get = null;

    public GuiManager(LBMain instance) {
        this.instance = instance;
    }

    private void createExit(Inventory inventory) {
        inventory.setItem(inventory.getSize() - 9, PlayerHead.CLOSE_WOOD.getHead(
                Message.GUI_EXIT.getAsString(true), Collections.singletonList("\u00a7f ")));
    }

    public void open(Player player, GuiType type) {
        if (type == GuiType.GET) {
            player.openInventory(get);
            return;
        }
        player.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7c" + type.name()
                + " gui feature is disabled or available only in premium version");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // -999 click out of container
        if (event.getSlot() < 0)
            return;

        if (get.equals(event.getClickedInventory())) {
            event.setCancelled(true);

            if (event.getSlot() == event.getClickedInventory().getSize() - 9) {
                event.getWhoClicked().closeInventory();
                return;
            }

            LuckyBlockType type = LuckyBlockAPI.parseLuckyBlock(event.getInventory().getItem(event.getSlot()));
            if (type == null || !type.isLoaded()) {
                return;
            }

            final Player player = ((Player) event.getWhoClicked());
            if (this.instance.config.get().getBoolean("permission-for-each-gui-get") &&
                    !player.hasPermission("luckyblock.get." + type.name().toLowerCase())
                    && !player.hasPermission("luckyblock.get.*")) {
                player.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%",
                        type.forceGet().getCustomName()));
                return;
            }

            final LuckyBlock block = type.forceGet();
            map.put(player, new CountGui(player, 1, 64, 1, (new ConfirmEvent() {

                @Override
                public void onConfirm(int amount) {

                    map.remove(player);

                    if (amount < 0) return;

                    if (!block.canBeSold()) {
                        for (int i = 0; i < amount; i++)
                            block.giveItem(player);
                        player.closeInventory();
                        return;
                    }

                    EconomyBridge economyBridge = GuiManager.this.instance.getEconomyBridge();
                    double current = economyBridge.getBalance(player);
                    double cost = block.getPrice() * amount;
                    if (current < cost) {
                        player.sendMessage(Message.GUI_GET_NOT_MONEY.getAsString().replace("%eco%",
                                economyBridge.format((int) (cost - current))));
                        player.closeInventory();
                    } else {
                        if (economyBridge.withdraw(player, (int) cost)) {
                            for (int i = 0; i < amount; i++)
                                block.giveItem(player);
                            player.sendMessage(Message.GUI_GET_SUCCESS.getAsString().replace("%eco%",
                                            economyBridge.format((int) cost))
                                    .replace("%amount%", String.valueOf(amount)));
                            player.openInventory(get);
                        } else {
                            player.closeInventory();
                        }
                    }

                }

                @Override
                public void goBack() {
                    map.remove(player);
                    player.openInventory(event.getInventory());
                }

            }), block.getSkull(), block.canBeSold(), block.getPrice()));
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.map.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.map.remove(event.getPlayer());
    }

    public void reload() {
        close();

        List<LuckyBlock> types = LuckyBlockType.enabled().stream()
                .sorted(Comparator.comparingInt(n -> n.asColor().getData()))
                .map(LuckyBlockType::forceGet)
                .collect(Collectors.toList());
        LBMain.debug("[GUIMANAGER] Found " + types.size() + " types");

        int rows = types.size() == 0 ? 3 : (int) Math.ceil(((double) types.size()) / 5);
        get = Bukkit.createInventory(null, (2 + rows) * 9, Message.GUI_GET_TITLE.getAsString(true));
        ItemStack grayPane = this.instance.factory.getItem(Mat.GRAY_PANE, 1);

        for (int row = 0; row < rows + 2; row++) {
            get.setItem(row * 9, grayPane);

            if (row - 1 == rows && !(LuckyBlockAPI.isPremium() && this.instance.disableAuthorInfo)) {
                ItemMeta meta = grayPane.getItemMeta();
                assert meta != null;
                meta.setLore(Arrays.asList("\u00a7f ", "\u00a7fRunning \u00a7eLuckyBlock NTD v"
                                + this.instance.getVersion() + " \u00a77("
                                + (LBMain.getVersionType().getColoredSimpleName()) + "\u00a77)",
                        "\u00a7fby\u00a7a danirod12 \u00a77(aka DenBeKKer)"));
                grayPane.setItemMeta(meta);
            }
            get.setItem(row * 9 + 8, grayPane);
        }

        grayPane = this.instance.factory.getItem(Mat.BLACK_PANE, 1);

        int amount = 0, slot = 11;
        for (LuckyBlock block : types) {
            get.setItem(slot, block.getSkull());

            amount++;
            slot++;

            if (amount >= 5) {
                slot += 4;
                amount = 0;
            }
        }

        while (amount != 0 && amount < 5) {
            get.setItem(slot, grayPane);
            amount++;
            slot++;
        }
        createExit(get);
    }

    public void close() {
        new ArrayList<>(map.keySet()).forEach(HumanEntity::closeInventory);
        map.clear();
        if (get != null) new ArrayList<>(get.getViewers()).forEach(HumanEntity::closeInventory);
    }

    public enum GuiType {

        EDIT, GET;

        public static GuiType parseGuiType(String name) {
            for (GuiType type : values())
                if (type.name().equalsIgnoreCase(name))
                    return type;
            return null;
        }

    }

}
