package me.DenBeKKer.ntdLuckyBlock;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockPlaceEvent;
import me.DenBeKKer.ntdLuckyBlock.util.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager.Message;

public class LBHandler implements Listener {
	
	@EventHandler
	public void piston(BlockPistonExtendEvent e) {
		
		if(LBMain.getInstance().pistonFix()) {
			e.getBlocks().forEach(n -> {
				if(LuckyBlockAPI.isLuckyBlock(n)) {
					e.setCancelled(true);
					return;
				}
			});
			
			if(e.getDirection() == BlockFace.UP) {
				if(LuckyBlockAPI.isLuckyBlock(e.getBlock().getLocation().add(0, 2, 0).getBlock())) {
					e.setCancelled(true);
					return;
				}
			}
			
		}
		
	}
	
	@EventHandler
	public void piston(BlockPistonRetractEvent e) {
		
		if(LBMain.getInstance().pistonFix()) {
			e.getBlocks().forEach(n -> {
				if(LuckyBlockAPI.isLuckyBlock(n)) {
					e.setCancelled(true);
					return;
				}
			});
			
//			if(e.getDirection() == BlockFace.UP) {
//				if(LuckyBlockAPI.isLuckyBlock(e.getBlock().getLocation().add(0, 2, 0).getBlock())) {
//					e.setCancelled(true);
//					return;
//				}
//			}
			
		}
		
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		GuiManager.ramFix(e.getPlayer());
		if(LBMain.getReduced() != null && LBMain.getReduced().contains(e.getPlayer()))
			LBMain.getReduced().remove(e.getPlayer());
	}
	
	@EventHandler
	public void explosion(EntityExplodeEvent e) {
		
		if(LBMain.getInstance().explosionFix())
			new ArrayList<>(e.blockList()).forEach(n -> {
				if(LuckyBlockAPI.isLuckyBlock(n)) {
					e.blockList().remove(n);
				}
			});
		
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		
		if(e.getPlayer().hasPermission("luckyblock.update") && LBMain.needUpdate() && LBMain.inform()) {
			
			new BukkitRunnable() {

				@Override
				public void run() {
					
					if(e.getPlayer() != null && e.getPlayer().isOnline()) {
						
						LBMain.getInstance().informAboutUpdate(e.getPlayer());
						
					}
					
				}
				
			}.runTaskLaterAsynchronously(LBMain.getInstance(), 40);
			
		}
		
	}
	
	@EventHandler
	public void place(BlockPlaceEvent e) {
		
		for(LuckyBlockType type : LuckyBlockType.list()) {
			
			try {
				if(e.getItemInHand().getItemMeta().getDisplayName().equals(type.get().getSkull().getItemMeta().getDisplayName())) {
					
					if(e.isCancelled()) return;
					
					LuckyBlockPlaceEvent event = new LuckyBlockPlaceEvent(e.getBlock(), e.getPlayer(), type);
					Bukkit.getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						e.setCancelled(true);
						return;
					}
					
					Bukkit.getScheduler().runTaskLater(LBMain.getInstance(), () -> {
						type.get().placeBlock(e.getBlock());
					}, 1);
					return;
					
				}
			} catch(Exception ignore) {
				
			}
			
		}
		
	}
	
	@EventHandler
	public void broke(BlockBreakEvent e) {
		
		if(e.getBlock().getType().name().toUpperCase().contains("STAINED_GLASS")) {
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
//				if((LBMain.getInstance().factory instanceof Mat1_13 && e.getBlock().getType() == type.getMaterial()) ||
//						(LBMain.getInstance().factory instanceof Mat1_12 && e.getBlock().getType() == type.getMaterial() &&
//								e.getBlock().getData() == type.asDye().ordinal())) {
					
					for(Entity en : e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(e.isCancelled()) return;
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(e.getBlock())) {
							
							/*
							 * 
							 *  Break luckyblock event
							 * 
							 */
							if(LBMain.getIsSk89q() && !LBWorldEdit.canBreak(e.getBlock())) {
								e.setCancelled(true);
								return;
							}
							
							if(LBMain.isBreakPermissions() && !e.getPlayer().hasPermission("luckyblock.break." + type.name().toLowerCase())
									&& !e.getPlayer().hasPermission("luckyblock.break.*")) {
								e.getPlayer().sendMessage(Message.CANT_BREAK_LUCKYBLOCK.getAsString().replace("%lb%", type.get() != null ? type.get().getCustomName() :
									type.name()));
								e.setCancelled(true);
								return;
							}
							
							if(type.get() != null) {
								if(type.get().tryOpen(e.getBlock(), e.getPlayer(), false))
									stand.remove();
								else e.setCancelled(true);
							} else stand.remove();
							return;
							
						}
						
					}
					
//				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void entity(PlayerArmorStandManipulateEvent e) {
		
		if(e.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			
			Block b = e.getRightClicked().getLocation().add(0, 1.2, 0).getBlock();
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				//if(b.getType() == type.getMaterial()) {
					
					ArmorStand stand = (ArmorStand) e.getRightClicked();
					if(stand.getCustomName() != null && stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
							+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())) {
						
						if(LBMain.getIsSk89q() && !LBWorldEdit.canBreak(b)) {
							e.setCancelled(true);
							return;
						}
						
						if(!e.getPlayer().hasPermission("luckyblock.break." + type.name().toLowerCase())) {
							e.getPlayer().sendMessage(Message.CANT_BREAK_LUCKYBLOCK.getAsString().replace("%lb%", type.get() != null ? type.get().getCustomName() :
								type.name()));
							e.setCancelled(true);
							return;
						}
						
						if(type.get() != null) {
							if(type.get().tryOpen(b, e.getPlayer(), false)) {
								stand.remove();
								b.setType(Material.AIR);
							}
						} else {
							stand.remove();
							b.setType(Material.AIR);
						}
						e.setCancelled(true);
						
					}
				
				//}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void entity(PlayerInteractEntityEvent e) {
		
		if(e.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			
			Block b = e.getRightClicked().getLocation().add(0, 1.2, 0).getBlock();
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				if(b.getType() == type.getItem().getType()) {
					
					ArmorStand stand = (ArmorStand) e.getRightClicked();
					if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
							+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())) {
						
						if(type.get() != null) {
							if(type.get().tryOpen(b, e.getPlayer(), false)) {
								stand.remove();
								b.setType(Material.AIR);
							}
						} else {
							stand.remove();
							b.setType(Material.AIR);
						}
						e.setCancelled(true);
						
					}
				
				}
				
			}
			
		}
		
	}
	
}
