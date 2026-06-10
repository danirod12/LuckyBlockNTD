package com.github.danirod12.luckyblock.listener;

import com.github.danirod12.luckyblock.api.event.LuckyBlockPlaceEvent;
import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import com.github.danirod12.luckyblock.api.model.LuckyBlock;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.util.Pair;
import com.github.danirod12.luckyblock.api.util.Single;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.hook.Hook;
import com.github.danirod12.luckyblock.hook.sk89q.WorldGuardProvider;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.SpigotUpdater;
import com.github.danirod12.luckyblock.util.StringMatcher;
import com.github.danirod12.luckyblock.util.config.ConfigHolder;
import com.github.danirod12.luckyblock.util.manager.MessagesManager;
import com.github.danirod12.luckyblock.variables.world.WorldListDataHandler;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused"})
public class CoreListener implements Listener {

    private final LuckyBlockEngine engine;
    private final SpigotUpdater spigotUpdater;
    private final WorldGuardProvider worldGuardProvider;
    private final Single<StringMatcher<WorldListDataHandler>> worldsFilter;
    private final Cache<Player, ItemStack> cache;
    private final ConfigHolder config;

    public CoreListener(LuckyBlockEngine engine, SpigotUpdater spigotUpdater, WorldGuardProvider worldGuardProvider,
                        Single<StringMatcher<WorldListDataHandler>> worldsFilter, ConfigHolder config) {
        this.engine = engine;
        this.spigotUpdater = spigotUpdater;
        this.worldGuardProvider = worldGuardProvider;
        this.worldsFilter = worldsFilter;
        this.config = config;
        switch (MinecraftVersion.getVersion().getPackageName()) {
            case "v1_19_R1":
            case "v1_19_R2":
                this.cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();
                engine.getLogChannel().warning("You are using bugged Spigot version 1.19.0-1.19.3!"
                        + " These versions contains critical issue that breaks plugins that uses PLAYER_HEADs."
                        + " Enabled beta fix for 1.19.0-1.19.3 using caches.");
                engine.getLogChannel().warning("It is recommended to update to 1.19.4!");
                break;
            default:
                this.cache = null;
        }
    }

    // 1.19.0-1.19.3 fix
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.cache == null) {
            return;
        }
        // This code part could be run only on 1.13+. Cache is not null, so we are sure
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getItem() == null
                || event.getItem().getType() != Material.PLAYER_HEAD) {
            return;
        }
        ItemStack helmet = event.getPlayer().getInventory().getItem(EquipmentSlot.HEAD);
        if (helmet != null && helmet.getType() != Material.AIR) {
            this.cache.put(event.getPlayer(), event.getItem());
            if (this.engine.getLogChannel().isDebug()) {
                String message = "Created cache for " + event.getPlayer().getName()
                        + " with item " + event.getItem().getType();
                event.getPlayer().sendMessage(message);
                this.engine.getLogChannel().debug(message);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        if (this.cache != null && stack.getType() == Material.AIR
                && event.getBlockPlaced().getType() == Material.PLAYER_HEAD) {
            stack = this.cache.getIfPresent(event.getPlayer());
            if (this.engine.getLogChannel().isDebug()) {
                String message = "Retrieved cache for " + event.getPlayer().getName()
                        + " with item " + (stack == null ? "null" : stack.getType().name());
                event.getPlayer().sendMessage(message);
                this.engine.getLogChannel().debug(message);
            }
        }

        Material requiredType = event.getBlockPlaced().getType();

        this.engine.parseLuckyBlock(stack).flatMap(this.engine::get).ifPresent(block -> {
            StringMatcher<WorldListDataHandler> matcher = this.worldsFilter.get();
            if (matcher != null && !matcher.isEnabled(event.getBlock().getWorld().getName())) {
                if (!matcher.getDataHandler().isPlaceAdmins()
                        && event.getPlayer().hasPermission("luckyblock.place.disabled")) {
                    event.getPlayer().sendMessage(MessagesManager.Message.CANT_INTERACT_WORLD.getAsString(true)
                            .replace("%world%", event.getBlock().getWorld().getName()));
                    event.setCancelled(true);
                    return;
                }
            }

            // TODO check if there is already a slab and move up or cancel event

            LuckyBlockPlaceEvent luckyBlockPlaceEvent = new LuckyBlockPlaceEvent(event,
                    event.getBlock(), event.getPlayer(), block.getKey());
            Bukkit.getPluginManager().callEvent(luckyBlockPlaceEvent);
            if (luckyBlockPlaceEvent.isCancelled()) {
                // I was thinking if we should cancel the event and decided to shift it to API client
                // We give the user the ability to accept event
                return;
            }

            // We cannot force set skull with another block and summon start there
            // Cannot say clear if it is only a legacy problem or not, but it is ok to have it on all versions
            SchedulerManager.runLaterAt(engine.getLogChannel().getPlugin(), event.getBlock().getLocation(), () -> {
                // Well, 1 tick may change a lot
                if (event.getBlock().getType() != requiredType) { // TODO check, make tests on all versions with heads
                    return;
                }
                block.placeBlock(event.getBlock());
                if (engine.getConfigHolder().forceUpdateInventory && event.getPlayer().isOnline()) {
                    event.getPlayer().updateInventory();
                }
            }, 1L);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakHighest(BlockBreakEvent event) {
        if (config.breakEventHighestPriority) {
            this.onBlockBreak(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreakHigh(BlockBreakEvent event) {
        if (!config.breakEventHighestPriority) {
            this.onBlockBreak(event);
        }
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (!engine.isLuckyBlock(event.getBlock().getType())) {
            return;
        }

        Pair<LuckyBlockKey, ArmorStand> pair = engine.searchByBlock(event.getBlock());
        if (pair != null) {
            event.setCancelled(true);
            if (Hook.WorldGuard.isEnabled() && !worldGuardProvider.canBreak(event.getBlock())) {
                return;
            }

            LuckyBlock block = engine.get(pair.getKey()).orElse(null);
            Player player = event.getPlayer();
            if (engine.getConfigHolder().breakPermissions && !Misc.hasPermission(player,
                    "luckyblock.break." + pair.getKey())) {
                player.sendMessage(MessagesManager.Message.CANT_BREAK_LUCKYBLOCK.getAsString().replace("%lb%",
                        block == null ? pair.getKey().getDefaultCustomName() : block.getCustomName()));
                return;
            }

            if (block != null) {
                boolean dropItems = true;
                if (!worldsFilter.get().isEnabled(event.getBlock().getWorld().getName())) {
                    if (worldsFilter.get().getDataHandler().isBreakNoDrops()) {
                        dropItems = false;
                    } else {
                        player.sendMessage(MessagesManager.Message.CANT_INTERACT_WORLD.getAsString(true)
                                .replace("%world%", event.getBlock().getWorld().getName()));
                        return;
                    }
                }
                if (block.playOpen(engine.getPlugin(), event.getBlock(), player, dropItems, false)) {
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
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (engine.isLuckyBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getDirection() == BlockFace.UP && engine.isLuckyBlock(event.getBlock()
                .getLocation().add(0, 2, 0).getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (engine.isLuckyBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(engine::isLuckyBlock);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(engine::isLuckyBlock);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        // This considered to be really often, we cannot check everything
        if (engine.isLuckyBlock(event.getBlock().getType()) && engine.isLuckyBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (engine.isLuckyBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (engine.getConfigHolder().informAboutUpdates
                && player.hasPermission("luckyblock.update") && spigotUpdater.isNeedUpdate()) {
            SchedulerManager.runLaterAt(engine.getLogChannel().getPlugin(), player.getLocation(), () -> {
                if (player.isOnline()) {
                    spigotUpdater.sendUpdateMessage(player);
                }
            }, 40L);
        }

        // No convertion at all on V3 ?
//        if (!engine.getConfigHolder().disableJsonConvertCheck && player.hasPermission("luckyblock.convert")) {
//            ConvertFactory factory = engine.getConvertFactory();
//            int convert = factory.getRequests();
//            if (convert > 0) {
//                if (LuckyBlockAPI.getVersionType().isPremium()) {
//                    player.sendMessage("§7[§eLuckyBlock§7] §fNew LuckyBlock configuration " +
//                            "update available! Now my plugin can store almost any item from any plugin and you can " +
//                            "set drop chances for each lucky entry. You can convert §c" + convert + "§f" +
//                            "entry drops to new JSON store format. §bPerform - §l/luckyblock convert");
//                    player.sendMessage("§4[*] §cTo prevent loss of configuration in case of error, make " +
//                            "backup of some files first");
//                    for (Map.Entry<Config, List<String>> entry : factory.getRequestMap().entrySet()) {
//                        player.sendMessage("§4 - §c" + entry.getKey().getName() + " §7(Have " +
//                                entry.getValue().size() + " unconverted items)");
//                    }
//                } else {
//                    player.sendMessage("§7[§eLuckyBlock§7] §fNew LuckyBlock configuration update available! Now " +
//                            "my plugin can store almost any item from any plugin and you can set drop chances for " +
//                            "each lucky entry. This cool feature available in§b premium version§f! You can convert " +
//                            "§c" + convert + "§f entry drops to new JSON store format. §bCheck out - " +
//                            "https://www.spigotmc.org/resources/94872/");
//                    player.sendMessage("§eNote:§7 You can disable this message in configuration");
//                }
//            }
//        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!this.engine.getConfigHolder().preventHatLuckyBlock) {
            return;
        }
        if (event.isCancelled() || event.getAction() == InventoryAction.NOTHING) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.ARMOR
                && event.getSlotType() != InventoryType.SlotType.CONTAINER
                && event.getSlotType() != InventoryType.SlotType.QUICKBAR) {
            return;
        }
        if (!event.getInventory().getType().equals(InventoryType.CRAFTING)
                && !event.getInventory().getType().equals(InventoryType.PLAYER)) {
            return;
        }

        boolean shift = event.getClick().name().contains("SHIFT");
        ItemStack stack = shift ? event.getCurrentItem() : event.getCursor();
        if (this.engine.isLuckyBlock(stack)) {
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
