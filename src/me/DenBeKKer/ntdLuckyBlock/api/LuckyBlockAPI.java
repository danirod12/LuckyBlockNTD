package me.DenBeKKer.ntdLuckyBlock.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop.LuckyItemType;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop.Special;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.CommandDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ConsoleDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.EntityDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.LuckyItemDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.MessageDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.SchematicDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.DiamondColumnSpecial;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.LightningSpecial;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.PigSpecial;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.WaterBucketSpecial;

public class LuckyBlockAPI {
	
	@SuppressWarnings("deprecation")
	public static LuckyDrop loadDrop(String drop0) {
		
		try {
			
			String[] sp = drop0.split(" : ");
			LuckyItemType type = LuckyItemType.valueOf(sp[0].toUpperCase());
			
			switch(type) {
			case COMMAND: return new CommandDrop(sp[1]);
			case CONSOLE: return new ConsoleDrop(sp[1]);
			case MESSAGE: return new MessageDrop(sp[1]);
			case ENTITY: {
				int i = 1;
				try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
				if(i < 1) i = 1;
				return new EntityDrop(EntityType.valueOf(sp[1].toUpperCase()), i);
			}
			case ITEM: {
				ItemStack item = new ItemStack(Material.matchMaterial(sp[1].toUpperCase()), Integer.parseInt(sp[2]), (short) Integer.parseInt(sp[3]));
				ItemMeta meta = item.getItemMeta();
				if(sp.length >= 5) meta.setDisplayName(sp[4].replace("&", "\u00a7"));
				if(sp.length > 6) meta.addEnchant(Enchantment.getByName(sp[5].toUpperCase()), Integer.parseInt(sp[6]), true);
				item.setItemMeta(meta);
				return new ItemDrop(item);
			}
			case LUCKY_BLOCK_ITEM: {
				int i = 1;
				try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
				if(i < 1) i = 1;
				return new LuckyItemDrop(LuckyBlockType.valueOf(sp[1].toUpperCase()), i);
			}
			case SCHEMATIC: {
				
				if(!LBMain.getIsSk89q()) {
					LBMain.getInstance().getLogger().log(Level.WARNING, "WorldEdit or WorldGuard was not found, lucky item \""
							+ drop0 + "\" wont be loaded");
					throw new UnsupportedOperationException();
				}
				
				boolean b = false;
				if(sp[2].equalsIgnoreCase("block"))
					b = true;
				else if(!sp[2].equalsIgnoreCase("player")) throw new UnsupportedOperationException();
				
				String file$name = sp[1].endsWith(".schem") ? sp[1] : sp[1] + ".schem";
				
				File file = new File(LBMain.getSchematicsFolder(), file$name);
				if(!file.exists()) {
					
					file$name = sp[1].endsWith(".schematic") ? sp[1] : sp[1] + ".schematic";
					file = new File(LBMain.getSchematicsFolder(), file$name);
					
					if(!file.exists()) {
						LBMain.getInstance().getLogger().log(Level.WARNING, "Schematic " + file.getPath() + " not found");
						return null;
					}
					
				}
				
				return new SchematicDrop(file, b);
				
			}
			case SPECIAL: {
				
				Special special = Special.valueOf(sp[1].toUpperCase());
				if(special == null) return null;
				
				switch(special) {
				case DIAMOND_COLUMN: {
					Collection<Material> collection = new ArrayList<>();
					for(int i = 2; i < sp.length; i++) {
						Material mat;
						try {
							mat = Material.valueOf(sp[i].toUpperCase());
						} catch(Exception ex) { LBMain.getInstance().getLogger().log(Level.WARNING, "Cant recognize material " + sp[i]); continue; }
						if(mat == null) continue;
						collection.add(mat);
					}
					return new DiamondColumnSpecial(collection);
				}
				case LIGHTNING: {
					int i = special.defaultValue();
					try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
					if(i < 1) i = special.defaultValue();
					return new LightningSpecial(i);
				}
				case PIG: {
					int i = special.defaultValue();
					try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
					if(i < 1) i = special.defaultValue();
					return new PigSpecial(i);
				}
				case WATER_BUCKET: {
					int i = special.defaultValue();
					try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
					if(i < 1) i = special.defaultValue();
					return new WaterBucketSpecial(i);
				}
				default:
					break;
				}
				
			}
			default: return null;
			}
			
		} catch(Exception ignored) { }
		return null;
		
	}
	
	public static void addLuckyDrop(LuckyBlockType type, LuckyDrop drop) throws LuckyBlockNotLoadedException { addLuckyDrop(type, Arrays.asList(drop)); }
	
	public static void addLuckyDrop(LuckyBlockType type, LuckyDrop... drop) throws LuckyBlockNotLoadedException {
		addLuckyDrop(type, Arrays.asList(drop));
	}
	
	public static void addLuckyDrop(LuckyBlockType type, Collection<LuckyDrop> drop) throws LuckyBlockNotLoadedException {
		
		LuckyBlock block = type.get();
		if(block == null)
			throw new LuckyBlockNotLoadedException(type);
		block.addIntent(drop);
		
	}
	
	@Deprecated
	public static me.DenBeKKer.ntdLuckyBlock.variables.LuckyItem loadItem(String item0) {
		throw new UnsupportedOperationException("LuckyItem no longer supported since 2.1.0! Use LuckyBlockAPI.loadDrop(String)");
//		try {
//			
//			String[] sp = item0.split(" : ");
//			
//			LuckyItemType type = LuckyItemType.valueOf(sp[0].toUpperCase());
//			
//			if(type == null) return null;
//			
//			switch(type) {
//			case ITEM:
//				
//				ItemStack item = new ItemStack(Material.matchMaterial(sp[1].toUpperCase()), Integer.parseInt(sp[2]), (short) Integer.parseInt(sp[3]));
//				ItemMeta meta = item.getItemMeta();
//				
//				if(sp.length >= 5) {
//					
//					meta.setDisplayName(sp[4].replace("&", "\u00a7"));
//					
//				}
//				
//				if(sp.length > 6) {
//
//					meta.addEnchant(Enchantment.getByName(sp[5].toUpperCase()), Integer.parseInt(sp[6]), true);
//					
//				}
//				return new LuckyItem(type, item, 0);
//				
//			case SPECIAL:
//				
//				Special special = Special.valueOf(sp[1].toUpperCase());
//				if(special == null) return null;
//				
//				if(special == Special.DIAMOND_COLUMN) {
//					
//					Collection<Material> collection = new ArrayList<>();
//					for(int i = 2; i < sp.length; i++) {
//						Material mat;
//						try {
//							mat = Material.valueOf(sp[i].toUpperCase());
//						} catch(Exception ex) { LBMain.getInstance().getLogger().log(Level.WARNING, "Can recognize material " + sp[i]); continue; }
//						if(mat == null) continue;
//						collection.add(mat);
//					}
//					
//					LuckyItem item6 = new LuckyItem(type, special, 0);
//					item6.special(collection);
//					return item6;
//					
//				}
//				int arg = -1;
//				try {
//					arg = Integer.parseInt(sp[2]);
//				} catch(Exception ignored) {}
//				if(arg <= 0) {
//					arg = special.defaultValue();
//				}
//				
//				return new LuckyItem(type, special, arg);
//				
//			case LUCKY_BLOCK_ITEM:
//				
//				int a = -1;
//				try {
//					a = Integer.parseInt(sp[2]);
//				} catch(Exception ignored) {}
//				if(a <= 0) a = 1;
//				return new LuckyItem(type, LuckyBlockType.valueOf(sp[1].toUpperCase()), a);
//			
//			case ENTITY:
//				
//				int a90 = -1;
//				try {
//					a90 = Integer.parseInt(sp[2]);
//				} catch(Exception ignored) {}
//				if(a90 <= 0) a90 = 1;
//				return new LuckyItem(type, EntityType.valueOf(sp[1].toUpperCase()), a90);
//				
//			case COMMAND: case CONSOLE: case MESSAGE:
//				
//				return new LuckyItem(type, sp[1], 0);
//				
//			default: return null;
//			}
//			
//		} catch(Exception ex) {
//			return null;
//		}
		
	}
	
	@Deprecated
	public static boolean isLuckyBlock(ItemStack item) {
		return getLuckyBlock(item) != null;
	}
	
	@Deprecated
	public static LuckyBlockType getLuckyBlock(ItemStack item) {
		
		return getLuckyBlock(item, false, true);
		
	}
	
	public static boolean isLuckyBlock(ItemStack item, boolean checkName, boolean checkUUID) {
		return getLuckyBlock(item, checkName, checkUUID) != null;
	}
	
	public static LuckyBlockType getLuckyBlock(ItemStack item, boolean checkName, boolean checkUUID) {
		
		UUID uuid = LBMain.getUUID(item);
		
		if(uuid == null || !(checkName || checkUUID)) return null;
		
		for(Entry<LuckyBlockType, LuckyBlock> s : LuckyBlockType.map().entrySet()) {
			
			if(checkUUID && !LBMain.getUUID(s.getValue().getSkull()).equals(uuid) ||
					checkName && !item.getItemMeta().getDisplayName().equalsIgnoreCase(s.getValue().getSkull().getItemMeta().getDisplayName())) continue;
			
			return s.getKey();
			
		}
		
		return null;
		
	}
	
	public static void placeLuckyBlock(Block b, LuckyBlockType type) throws LuckyBlockNotLoadedException {
		
		if(type.get() != null) {
			type.get().placeBlock(b, false);
		} else throw new LuckyBlockNotLoadedException(type);
		
	}
	
	public static boolean isLuckyBlock(Block b) {
		
		if(b.getType().name().toUpperCase().contains("STAINED_GLASS")) {
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				//if(e.getBlock().getType() == type.getMaterial()) {
					
					for(Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {
							
							return true;
							
						}
						
					}
					
				//}
				
			}
			
		}
		return false;
		
	}
	
	@Deprecated
	public static void breakLuckyBlock0(Block b) { breakLuckyBlock0(b, false); }
	
	public static void breakLuckyBlock0(Block b, boolean ignore) {
		
		if(b.getType().name().toUpperCase().contains("STAINED_GLASS")) {
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				//if(e.getBlock().getType() == type.getMaterial()) {
					
					for(Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {
							
							if(type.get() != null) {
								if(type.get().tryOpen(b, ignore)) {
									stand.remove();
									b.setType(Material.AIR);
								}
							} else {
								stand.remove();
								b.setType(Material.AIR);
							}
							
							return;
							
						}
						
					}
					
				//}
				
			}
			
		}
		
	}
	
	@Deprecated
	public static void breakLuckyBlock(Block b, Player p) { breakLuckyBlock(b, p, true); }
	
	@Deprecated
	public static void breakLuckyBlock(Block b, Player p, boolean drop) { breakLuckyBlock(b, p, drop, false); }
	
	public static void breakLuckyBlock(Block b, Player p, boolean drop, boolean ignore) {
		
		if(b.getType().name().toUpperCase().contains("STAINED_GLASS")) {
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				//if(e.getBlock().getType() == type.getMaterial()) {
					
					for(Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {
							
							if(drop) {
								if(type.get() != null) {
									if(p == null) {
										throw new NullPointerException(
												"You must provide player for breakLuckyBlock(Block, Player, boolean) or use breakLuckyBlock0(Block)");
									}
									if(type.get().tryOpen(b, p, ignore)) {
										stand.remove();
										b.setType(Material.AIR);
									}
								} else {
									stand.remove();
									b.setType(Material.AIR);
								}
							} else {
								stand.remove();
								b.setType(Material.AIR);
							}
							
							return;
							
						}
						
					}
					
				//}
				
			}
			
		}
		
	}
	
	public void destroy(LuckyBlockType type) {
		LuckyBlockType.map().put(type, null);
	}
	
	public void destroy(LuckyBlock luckyblock) {
		LuckyBlockType.map().put(luckyblock.getType(), null);
	}
	
	public void rewrite(LuckyBlock luckyblock) {
		LuckyBlockType.map().put(luckyblock.getType(), luckyblock);
	}
	
	public void system_load(Plugin your_plugin_instance) {
		if(your_plugin_instance.equals(LBMain.getInstance()))
			throw new UnsupportedOperationException("Provide your plugin instance, not NTD LuckyBlock");
		LBMain.log(Level.WARNING, "Loading system by plugin " + your_plugin_instance.getName() + " v" + your_plugin_instance.getDescription().getVersion());
		LBMain.getInstance().system_load();
	}
	
	public String getVersion() { return LBMain.getVersion(); }
	public String getLastUpdate() { return LBMain.getLastUpdate(); }
	public int getBuild() { return LBMain.getBuild(); }
	
	public LBMain getMainInstance() { return LBMain.getInstance(); }
	
	public boolean isPremium() { return LBMain.isPremium(); }
	
}
