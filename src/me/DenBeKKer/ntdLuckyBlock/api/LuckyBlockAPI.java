package me.DenBeKKer.ntdLuckyBlock.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.loader.PathLoader;
import me.DenBeKKer.ntdLuckyBlock.api.loader.StringLoader;
import me.DenBeKKer.ntdLuckyBlock.loader.ConvertManager;
import me.DenBeKKer.ntdLuckyBlock.loader.JSONLoader;
import me.DenBeKKer.ntdLuckyBlock.loader.LegacyLoader;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;

public class LuckyBlockAPI {
	
	private final static StringLoader LEGACY = new LegacyLoader();
	private final static PathLoader JSON = new JSONLoader();
	
	public static StringLoader getLegacyLoader() {
		return LEGACY;
	}
	
	public static PathLoader getJSONLoader() {
		return JSON;
	}
	
	public static LuckyEntry loadDropEntry(Config loaded, String path) {
		
		if(loaded == null) throw new NullPointerException("Config is unloaded");
		
		if(loaded.get().isSet(path + ".items")) {
			
			if(!LBMain.isPremium()) {
				LBMain.log(Level.SEVERE, "Configuration path " + loaded.getName() + "/" + path
					+ " was converted to JSON format. Free version supports only legacy formats");
				return new LuckyEntry(DropChance.MEDIUM, new ItemDrop(new ItemStack(Material.STONE)));
			}
			
			String ch = loaded.get().getString(path + ".chance");
			DropChance chance = DropChance.MEDIUM;
			try {
				chance = DropChance.valueOf(ch);
			} catch(Exception ex) {
				LBMain.log(Level.WARNING, "Drop chance \""
						+ (ch == null ? "null" : ch) + "\" is unsupported (" + loaded.getName() + ", " + path + ".chance)");
			}
			LuckyEntry entry = new LuckyEntry(chance);
			for(String drop0 : loaded.get().getConfigurationSection(path + ".items").getKeys(false)) {
				
				try {
					LuckyDrop drop = JSON.load(loaded, path + ".items." + drop0);
					if(drop != null)
						entry.add(drop);
				} catch(Throwable th) {
					
					if(th instanceof ClassNotFoundException) {
						LBMain.log(Level.WARNING, th.getLocalizedMessage());
					}
					
					if(LBMain.isDebug())
						th.printStackTrace();
					
				}
				
			}
			return entry;
			
		}
		
		if(loaded.get().isSet(path)) {
			
			List<String> list = loaded.get().getStringList(path);
			LuckyEntry entry = new LuckyEntry();
			for(String drop0 : list) {
				
				LuckyDrop drop = LEGACY.load(drop0);
				if(drop != null)
					entry.add(drop);
				
			}
			ConvertManager.add(loaded, path);
			return entry;
			
		}
		
		return null;
		
	}
	
	@Deprecated
	public static LuckyDrop loadDrop(String drop0) { return LEGACY.load(drop0); }
	
	@Deprecated
	public static void addLuckyDrop(LuckyBlockType type, LuckyDrop drop) throws LuckyBlockNotLoadedException {
		addLuckyDrop(type, Arrays.asList(drop));
	}
	
	@Deprecated
	public static void addLuckyDrop(LuckyBlockType type, LuckyDrop... drop) throws LuckyBlockNotLoadedException {
		addLuckyDrop(type, Arrays.asList(drop));
	}
	
	@Deprecated
	public static void addLuckyDrop(LuckyBlockType type, Collection<LuckyDrop> drop) throws LuckyBlockNotLoadedException {
		
		LuckyBlock block = type.get();
		block.addIntent(new LuckyEntry(drop));
		
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
			
			if(checkName && !item.getItemMeta().getDisplayName().equalsIgnoreCase(s.getValue().getSkull().getItemMeta().getDisplayName())
					|| checkUUID && !s.getKey().getUUID().equals(uuid)) continue;
			
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
		
		if(b.getType().name().toUpperCase().contains("STAINED_GLASS") ||
				b.getType().name().equalsIgnoreCase("TINTED_GLASS")) {
			
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
		
		if(b.getType().name().toUpperCase().contains("STAINED_GLASS") ||
				b.getType().name().equalsIgnoreCase("TINTED_GLASS")) {
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				//if(e.getBlock().getType() == type.getMaterial()) {
					
					for(Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {
							
							if(type.isLoaded()) {
								try {
									if(type.get().tryOpen(b, ignore)) {
										stand.remove();
										b.setType(Material.AIR);
									}
								} catch (LuckyBlockNotLoadedException e) {
									e.printStackTrace();
									return;
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
		
		if(b.getType().name().toUpperCase().contains("STAINED_GLASS") ||
				b.getType().name().equalsIgnoreCase("TINTED_GLASS")) {
			
			for(LuckyBlockType type : LuckyBlockType.values()) {
				
				//if(e.getBlock().getType() == type.getMaterial()) {
					
					for(Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {
						
						if(en.getType() != EntityType.ARMOR_STAND) continue;
						ArmorStand stand = (ArmorStand) en;
						if(stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int)stand.getLocation().getX()
								+ ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ())
								&& stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {
							
							if(drop) {
								if(type.isLoaded()) {
									if(p == null) {
										throw new NullPointerException(
												"You must provide player for breakLuckyBlock(Block, Player, boolean) or use breakLuckyBlock0(Block)");
									}
									try {
										if(type.get().tryOpen(b, p, ignore)) {
											stand.remove();
											b.setType(Material.AIR);
										}
									} catch (LuckyBlockNotLoadedException e) {
										e.printStackTrace();
										return;
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
	
	public static void destroy(LuckyBlockType type) {
		LuckyBlockType.map().put(type, null);
	}
	
	public static void destroy(LuckyBlock luckyblock) {
		LuckyBlockType.map().put(luckyblock.getType(), null);
	}
	
	public static void rewrite(LuckyBlock luckyblock) {
		LuckyBlockType.map().put(luckyblock.getType(), luckyblock);
	}
	
	public static void system_load(Plugin your_plugin_instance) {
		if(your_plugin_instance.equals(LBMain.getInstance()))
			throw new UnsupportedOperationException("Provide your plugin instance, not NTD LuckyBlock");
		LBMain.log(Level.WARNING, "Loading system by plugin " + your_plugin_instance.getName() + " v" + your_plugin_instance.getDescription().getVersion());
		LBMain.getInstance().system_load();
	}
	
	public static String getVersion() { return LBMain.getVersion(); }
	public static String getLastUpdate() { return LBMain.getLastUpdate(); }
	public static int getBuild() { return LBMain.getBuild(); }
	
	public static LBMain getMainInstance() { return LBMain.getInstance(); }
	
	public static boolean isPremium() { return LBMain.isPremium(); }
	
}
