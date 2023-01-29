package me.DenBeKKer.ntdLuckyBlock.listener;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.events.LuckyBlockPlaceEvent;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldGuard;
import me.DenBeKKer.ntdLuckyBlock.loader.ConvertManager;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Pair;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
public class CoreListener implements Listener {

    private final LBMain instance;

    public CoreListener(LBMain instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        LuckyBlockType temp = null;
        if (instance.getConvertManager().isFactoryEnabled() && instance.getConvertManager().isVerifyTAG()) {
            temp = LuckyBlockAPI.parseOldLuckyBlock(event.getItemInHand());
            if (temp != null) {
                Bukkit.getScheduler().runTaskLater(instance,
                        () -> ConvertManager.convert(event.getPlayer()), 1L);
            }
        }

        LuckyBlockType type = temp != null ? temp : LuckyBlockAPI.parseLuckyBlock(event.getItemInHand(),
                instance.getConvertManager().isVerifyUUID(),
                instance.getConvertManager().isVerifyTAG());
        if (type != null && type.isLoaded()) {
            if (event.isCancelled()) {
                return;
            }

            if (!instance.worldsFilter.isEnabled(event.getBlock().getWorld().getName())) {
                if (!instance.worldsFilter.getDataHandler().getPlaceAdmins()
                        && event.getPlayer().hasPermission("luckyblock.place.disabled")) {
                    event.getPlayer().sendMessage(MessagesManager.Message.CANT_INTERACT_WORLD.getAsString(true)
                            .replace("%world%", event.getBlock().getWorld().getName()));
                    event.setCancelled(true);
                    return;
                }
            }

            LuckyBlockPlaceEvent apiEventCall = new LuckyBlockPlaceEvent(event.getBlock(), event.getPlayer(), type);
            Bukkit.getPluginManager().callEvent(apiEventCall);
            if (apiEventCall.isCancelled()) return;
            Bukkit.getScheduler().runTaskLater(instance, () -> {
                LuckyBlockType.map().get(type).placeBlock(event.getBlock(), true);
                if (instance.forceUpdateInventory && event.getPlayer().isOnline())
                    event.getPlayer().updateInventory();
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String blockType = event.getBlock().getType().name();
        if (!blockType.toUpperCase().contains("STAINED_GLASS") &&
                !blockType.equalsIgnoreCase("TINTED_GLASS") &&
                event.getBlock().getType() != Material.ICE) {
            return;
        }

        Pair<LuckyBlockType, Entity> pair = LuckyBlockAPI.searchByBlock(event.getBlock());
        if (pair != null) {
            event.setCancelled(true);
            if (Hook.WorldGuard.isEnabled() && !LBWorldGuard.canBreak(event.getBlock())) {
                return;
            }

            Player player = event.getPlayer();
            if (instance.breakPermissions && !player.hasPermission("luckyblock.break."
                    + pair.getKey().name().toLowerCase()) && !player.hasPermission("luckyblock.break.*")) {
                player.sendMessage(MessagesManager.Message.CANT_BREAK_LUCKYBLOCK.getAsString()
                        .replace("%lb%", pair.getKey().getCustomName(true)));
                return;
            }

            if (pair.getKey().isLoaded()) {
                if (LBMain.LuckyBlockType.map().get(pair.getKey()).tryOpen(event.getBlock(), player, false)) {
                    event.getBlock().setType(Material.AIR);
                    pair.getValue().remove();
                }
            } else {
                event.setCancelled(false);
                pair.getValue().remove();
            }
        }
    }

    @EventHandler
    public void noBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (LuckyBlockAPI.isLuckyBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getDirection() == BlockFace.UP && LuckyBlockAPI.isLuckyBlock(event.getBlock()
                .getLocation().add(0, 2, 0).getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (LuckyBlockAPI.isLuckyBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(LuckyBlockAPI::isLuckyBlock);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE
                && LuckyBlockAPI.isLuckyBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(LuckyBlockAPI::isLuckyBlock);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (instance.informAboutUpdates && player.hasPermission("luckyblock.update")
                && instance.getUpdater().need_update$cache()) {
            Bukkit.getScheduler().runTaskLater(instance, () -> {
                if (player.isOnline()) {
                    instance.getUpdater().announce(player);
                }
            }, 40L);
        }

        if (!instance.disableConvertCheck && player.hasPermission("luckyblock.convert")) {
            ConvertManager manager = instance.getConvertManager();
            int convert = manager.getRequests();
            if (convert > 0) {

                if (LBMain.isPremium()) {
                    player.sendMessage("§7[§eLuckyBlock§7] §fNew LuckyBlock configuration " +
                            "update available! Now my plugin can store almost any item from any plugin and you can " +
                            "set drop chances for each lucky entry. You can convert §c" + convert + "§f" +
                            "entry drops to new JSON store format. §bPerform - §l/luckyblock convert");
                    player.sendMessage("§4[*] §cTo prevent loss of configuration in case of error, make " +
                            "backup of some files first");
                    for (Map.Entry<Config, List<String>> entry : manager.getRequestMap().entrySet())
                        player.sendMessage("§4 - §c" + entry.getKey().getName() + " §7(Have " +
                                entry.getValue().size() + " unconverted items)");
                } else {
                    player.sendMessage("§7[§eLuckyBlock§7] §fNew LuckyBlock configuration update available! Now " +
                            "my plugin can store almost any item from any plugin and you can set drop chances for " +
                            "each lucky entry. This cool feature available in§b premium version§f! You can convert " +
                            "§c" + convert + "§f entry drops to new JSON store format. §bCheck out - " +
                            "https://www.spigotmc.org/resources/94872/");
                    player.sendMessage("§eNote:§7 You can disable this message in configuration");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        instance.getGuiManager().gc(event.getPlayer());
        instance.getCommandsManager().gc(event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerPickupItem(org.bukkit.event.player.PlayerPickupItemEvent event) {
        Item item = event.getItem();
        LuckyBlockType type = LuckyBlockAPI.parseLuckyBlock(item.getItemStack(), false);
        if (type != null && type.isLoaded()) {
            ItemStack stack = LuckyBlockType.map().get(type).getSkull();
            stack.setAmount(item.getItemStack().getAmount());
            item.setItemStack(stack);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!instance.preventHatLB) return;
        if (event.isCancelled() || event.getAction() == InventoryAction.NOTHING) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getSlotType() != InventoryType.SlotType.ARMOR &&
                event.getSlotType() != InventoryType.SlotType.CONTAINER &&
                event.getSlotType() != InventoryType.SlotType.QUICKBAR) {
            return;
        }
        if (!event.getInventory().getType().equals(InventoryType.CRAFTING) &&
                !event.getInventory().getType().equals(InventoryType.PLAYER)) {
            return;
        }

        boolean shift = event.getClick().name().contains("SHIFT");
        ItemStack stack = shift ? event.getCurrentItem() : event.getCursor();
        if (LuckyBlockAPI.checkLuckyBlock(stack)) {
            if (shift) {
                ItemStack helmet = event.getWhoClicked().getInventory().getHelmet();
                if (helmet == null || helmet.getType() == Material.AIR) {
                    event.setCancelled(true);
                }
            } else if (event.getRawSlot() == 5) {
                event.setCancelled(true);
            }
        }
    }

}
