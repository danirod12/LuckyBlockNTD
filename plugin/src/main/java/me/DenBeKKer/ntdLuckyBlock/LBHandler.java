package me.DenBeKKer.ntdLuckyBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.events.LuckyBlockPlaceEvent;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.customitem.HitEvent;
import me.DenBeKKer.ntdLuckyBlock.loader.ConvertManager;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldGuard;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class LBHandler implements Listener {
	
	@EventHandler
	public void piston(BlockPistonExtendEvent e) {

		for(Block block : e.getBlocks()) {
			if(LuckyBlockAPI.isLuckyBlock(block)) {
				e.setCancelled(true);
				return;
			}
		}
		
		if(e.getDirection() == BlockFace.UP) {
			if(LuckyBlockAPI.isLuckyBlock(e.getBlock().getLocation().add(0, 2, 0).getBlock())) {
				e.setCancelled(true);
				return;
			}
		}

	}
	
	@EventHandler
	public void piston(BlockPistonRetractEvent e) {

		for(Block block : e.getBlocks()) {
			if(LuckyBlockAPI.isLuckyBlock(block)) {
				e.setCancelled(true);
				return;
			}
		}

	}

	@EventHandler
	public void move(PlayerMoveEvent e) {

		if(CustomItemFactory.chilly_pants &&
				CustomItemFactory.compare(e.getPlayer().getInventory().getLeggings(), "ntdluckyblock-chilly_pants")) {

			if(e.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

			Block target = e.getTo().clone().add(.0D, -1.0D, .0D).getBlock();
			if(target.getType() == Material.ICE) return;
			if(CustomItemFactory.solid && !target.getType().isSolid()) return;
			target.setType(Material.ICE);

		}

	}

	@EventHandler
	public void hit(EntityDamageByEntityEvent event) {
		
		victim:
		if(event.getEntity() instanceof Player) {
			
			final Player player = (Player) event.getEntity();

			if(CustomItemFactory.rage_armor && CustomItemFactory.isRageArmor(player))
				event.setDamage(event.getDamage() * (100 - CustomItemFactory.rage_armor_percentage));

			final ItemStack stack = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
			if(stack == null) break victim;
			BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
			if(item == null) break victim;
			item.handle(new HitEvent(event.getDamager(), player, HitEvent.Type.VICTIM));
			
		}
		damager:
		if(event.getDamager() instanceof Player) {
			
			final Player player = (Player) event.getDamager();
			final ItemStack stack = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
			if(stack == null) break damager;
			BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
			if(item == null) break damager;
			item.handle(new HitEvent(player, event.getEntity(), HitEvent.Type.DAMAGER));
			
		}
		
	}
	
	@EventHandler
	public void consume(PlayerItemConsumeEvent e) {
		
		BekkerItemStack item = CustomItemFactory.fetchCustomItem(e.getItem());
		if(item == null) return;
		item.handle(e);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventory(InventoryClickEvent e) {
		
		if(!LBMain.isPreventSkulls()) return;
		if(e.isCancelled() || e.getAction() == InventoryAction.NOTHING) return;
		if(!(e.getWhoClicked() instanceof Player)) return;
		
		if(e.getSlotType() != SlotType.ARMOR && e.getSlotType() != SlotType.CONTAINER
				&& e.getSlotType() != SlotType.QUICKBAR) return;
		
		if(!e.getInventory().getType().equals(InventoryType.CRAFTING) &&
				!e.getInventory().getType().equals(InventoryType.PLAYER)) return;
		final boolean shift = e.getClick().name().contains("SHIFT");
		
		final ItemStack stack = shift ? e.getCurrentItem() : e.getCursor();
		if(LuckyBlockAPI.checkLuckyBlock(stack)) {
			
			if(shift) {
				if(isAirOrNull(e.getWhoClicked().getInventory().getHelmet()))
					e.setCancelled(true);
				return;
			} else if(e.getRawSlot() == 5) e.setCancelled(true);
			
		}
		
	}
	
	private boolean isAirOrNull(ItemStack helmet) {
		return helmet == null || helmet.getType() == Material.AIR;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void claim_drop(org.bukkit.event.player.PlayerPickupItemEvent e) {
		
		LBMain.LuckyBlockType type = LuckyBlockAPI.parseLuckyBlock(e.getItem().getItemStack(), false);
		if(type != null && type.isLoaded()) {
			
			ItemStack item = LBMain.LuckyBlockType.map().get(type).getSkull();
			item.setAmount(e.getItem().getItemStack().getAmount());
			e.getItem().setItemStack(item);
			
		}
		
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		
		try {
			if(e.getHand() != EquipmentSlot.HAND) return;
		} catch(Throwable v1_8) {}
		final ItemStack stack = e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot());
		if(stack == null) return;
		BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
		if(item == null) return;
		item.handle(e);
		
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		GuiManager.ramFix(e.getPlayer());
		LBMain.getCommandsManager().fix_ram(e.getPlayer());
	}
	
	@EventHandler
	public void explosion(EntityExplodeEvent e) {
		e.blockList().removeIf(LuckyBlockAPI::isLuckyBlock);
	}

	@EventHandler
	public void fade(BlockFadeEvent e) {
		if(e.getBlock().getType() == Material.ICE && LuckyBlockAPI.isLuckyBlock(e.getBlock())) e.setCancelled(true);
	}

	@EventHandler
	public void explosion(BlockExplodeEvent e) {
		e.blockList().removeIf(LuckyBlockAPI::isLuckyBlock);
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		
		if(e.getPlayer().hasPermission("luckyblock.update") && LBMain.getUpdater().need_update$cache() && LBMain.isInformAboutUpdates()) {
			
			new BukkitRunnable() {

				@Override
				public void run() {
					
					if(e.getPlayer().isOnline())
						LBMain.getUpdater().announce(e.getPlayer());
					
				}
				
			}.runTaskLaterAsynchronously(LBMain.getInstance(), 40);
			
		}
		if(e.getPlayer().hasPermission("luckyblock.warning-luckyblock-changed") &&
				LBMain.warning_luckyblock_changed()) {
			
			Bukkit.getScheduler().runTaskLaterAsynchronously(LBMain.getInstance(), () -> {
				
				if(e.getPlayer() != null && e.getPlayer().isOnline()) {
					
					e.getPlayer().sendMessage("\u00a7c\u00a7l[WARNING] \u00a77[\u00a7eLuckyBlock\u00a77] \u00a76Due LuckyBlock matching method"
							+ " changing, some luckyblocks (That not been generated by plugin method \u00a7eLuckyBlock#getSkull()\u00a76) may wont"
							+ " work. To fix this issue you must disable config.yml option \u00a7cplace.verify-UUID\u00a76. But it not recommended."
							+ " \u00a77(This message wont appear after server restart or reload and only players with permission \u00a78luckyblock."
							+ "warning-luckyblock-changed\u00a77 see it)");
					e.getPlayer().sendMessage("\u00a7c\u00a7l[!] \u00a7fFor more information visit \u00a7bhttps://clck.ru/XC7L7");
					
				}
				
			}, 50);
			
		}
		if(e.getPlayer().hasPermission("luckyblock.convert") && !LBMain.getInstance().reduce_convert) {
			
			int convert = LBMain.getConvertManager().getRequests();
			if(convert > 0) {
				
				if(LBMain.isPremium()) {
					e.getPlayer().sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fNew luckyblock configuration update available! Now " +
							"my plugin can store almost any item from any plugin and you can set drop chances for each lucky entry. You can " +
							"convert \u00a7c" + convert + " \u00a7fentry drops to new JSON store format. \u00a7bPerform - \u00a7l/luckyblock convert");
					e.getPlayer().sendMessage("\u00a74[*] \u00a7cTo prevent loss of configuration in case of error, make backup of some files first");
					for(Entry<Config, Collection<String>> entry : LBMain.getConvertManager().getRequestMap().entrySet())
						e.getPlayer().sendMessage("\u00a74 - \u00a7c" + entry.getKey().getName()
								+ " \u00a77(Have " + entry.getValue().size() + " unconverted items)");
				} else {
					e.getPlayer().sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fNew luckyblock configuration update available! Now " +
							"my plugin can store almost any item from any plugin and you can set drop chances for each lucky entry. This " +
							"cool feature available in \u00a7bpremium version\u00a7f! You can convert \u00a7c" + convert + " \u00a7fentry " +
							"drops to new JSON store format. \u00a7bCheck out - https://www.spigotmc.org/resources/94872/");
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void place(BlockPlaceEvent e) {
		
		LBMain.LuckyBlockType temp = null;
		if(LBMain.getConvertManager().isFactory() && LBMain.getConvertManager().isVerifyTAG()) {
			temp = LuckyBlockAPI.parseOldLuckyBlock(e.getItemInHand());
			if(temp != null) Bukkit.getScheduler().runTaskLater(LBMain.getInstance(), () -> ConvertManager.convert(e.getPlayer()), 1L);
		}
		
		final LBMain.LuckyBlockType type = temp != null ? temp : LuckyBlockAPI.parseLuckyBlock(e.getItemInHand(), LBMain.getConvertManager().isVerifyUUID(),
				LBMain.getConvertManager().isVerifyTAG());
		if(type != null && type.isLoaded()) {
			
			if(e.isCancelled()) return;
			if(!LBMain.getInstance().w.allowed_break(e.getBlock().getWorld())) {
				if(!LBMain.getInstance().w.getPlaceAdmins() && e.getPlayer().hasPermission("luckyblock.place.disabled")) {
					e.getPlayer().sendMessage(Message.CANT_INTERACT_WORLD.getAsString(true).replace("%world%", e.getBlock().getWorld().getName()));
					e.setCancelled(true);
					return;
				}
			}
			
			LuckyBlockPlaceEvent event = new LuckyBlockPlaceEvent(e.getBlock(), e.getPlayer(), type);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) return;
			Bukkit.getScheduler().runTaskLater(LBMain.getInstance(), () -> LBMain.LuckyBlockType.map().get(type).placeBlock(e.getBlock(), true), 1L);
			return;
			
		}
		final ItemStack stack = e.getItemInHand();
		if(stack == null) return;
		BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
		if(item == null) return;
		item.handle(e);
		
	}
	
	@EventHandler
	public void broke(BlockBreakEvent e) {
		
		if(e.getBlock().getType().name().toUpperCase().contains("STAINED_GLASS") ||
				e.getBlock().getType().name().equalsIgnoreCase("TINTED_GLASS") || e.getBlock().getType() == Material.ICE) {
			
			for(LBMain.LuckyBlockType type : LBMain.LuckyBlockType.values()) {
				
					for(Entity en : e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(e.isCancelled()) return;
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName() == null) continue;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(e.getBlock())) {
							
							if(LBMain.getIsSk89q() && !LBWorldGuard.canBreak(e.getBlock())) {
								e.setCancelled(true);
								return;
							}
							
							if(LBMain.isBreakPermissions() && !e.getPlayer().hasPermission("luckyblock.break." + type.name().toLowerCase())
									&& !e.getPlayer().hasPermission("luckyblock.break.*")) {
								e.getPlayer().sendMessage(Message.CANT_BREAK_LUCKYBLOCK.getAsString().replace("%lb%",
										type.getCustomName(true)));
								e.setCancelled(true);
								return;
							}
							
							if(type.isLoaded()) {
								try {
									e.setDropItems(false);
								} catch(Throwable v1_8) { }
								if(type.map().get(type).tryOpen(e.getBlock(), e.getPlayer(), false))
									stand.remove();
								else e.setCancelled(true);
							} else stand.remove();
							return;
							
						}
						
					}
				
			}
			
		}
		
		final ItemStack stack = e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot());
		if(stack == null) return;
		BekkerItemStack item = CustomItemFactory.fetchCustomItem(stack);
		if(item == null) return;
		item.handle(e);
		
	}
	
}
