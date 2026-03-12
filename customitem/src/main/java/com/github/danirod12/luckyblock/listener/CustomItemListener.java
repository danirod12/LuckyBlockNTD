package com.github.danirod12.luckyblock.listener;

import com.github.danirod12.luckyblock.api.customitem.BekkerItemStack;
import com.github.danirod12.luckyblock.api.customitem.CustomItemFactory;
import com.github.danirod12.luckyblock.api.customitem.HitEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CustomItemListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (CustomItemFactory.chillyPants && CustomItemFactory.compare(event.getPlayer()
                .getInventory().getLeggings(), "ntdluckyblock-chilly_pants")) {
            if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            Block target = event.getTo().clone().add(.0D, -1.0D, .0D).getBlock();
            if (target.getType() == Material.ICE) {
                return;
            }
            if (CustomItemFactory.solid && !target.getType().isSolid()) {
                return;
            }
            target.setType(Material.ICE);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        victim:
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (CustomItemFactory.rageArmor && CustomItemFactory.isRageArmor(player)) {
                event.setDamage(event.getDamage() * (100 - CustomItemFactory.rageArmorPercentage));
            }

            final ItemStack stack = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (stack == null) {
                break victim;
            }
            BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
            if (item == null) {
                break victim;
            }
            item.handle(new HitEvent(event.getDamager(), player, HitEvent.Type.VICTIM));
        }

        damager:
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack stack = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (stack == null) {
                break damager;
            }
            BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
            if (item == null) {
                break damager;
            }
            item.handle(new HitEvent(player, event.getEntity(), HitEvent.Type.DAMAGER));
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        BekkerItemStack item = CustomItemFactory.fetchCustomItem(event.getItem());
        if (item == null) {
            return;
        }
        item.handle(event);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        try {
            if (event.getHand() != EquipmentSlot.HAND) {
                return;
            }
        } catch (Throwable ignored) {
        }
        PlayerInventory inventory = event.getPlayer().getInventory();
        final ItemStack stack = inventory.getItem(inventory.getHeldItemSlot());
        if (stack == null) {
            return;
        }
        BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
        if (item == null) {
            return;
        }
        item.handle(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        if (stack == null) {
            return;
        }
        BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
        if (item == null) {
            return;
        }
        item.handle(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        final ItemStack stack = inventory.getItem(inventory.getHeldItemSlot());
        if (stack == null) {
            return;
        }
        BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
        if (item == null) {
            return;
        }
        item.handle(event);
    }
}
