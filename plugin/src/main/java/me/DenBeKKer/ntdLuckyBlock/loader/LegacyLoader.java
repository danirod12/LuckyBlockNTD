package me.DenBeKKer.ntdLuckyBlock.loader;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.loader.StringLoader;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop.LuckyItemType;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop.Special;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.*;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

public class LegacyLoader implements StringLoader {
	
	@SuppressWarnings("deprecation")
	public LuckyDrop load(String drop) {
		
		try {
			
			String[] sp = drop.split(" : ");
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
				for(int ench = 5; ench < sp.length - 1; ench += 2) {
					try {
						meta.addEnchant(Enchantment.getByName(sp[ench].toUpperCase()), Integer.parseInt(sp[ench + 1]), true);
					} catch(Exception ex) {
						ex.printStackTrace();
						LBMain.log(Level.WARNING, "Enchantment " + sp[ench].toUpperCase() + " (level: " + sp[ench + 1]
								+ " cannot be added to " + item.getType().name());
					}
				}
				item.setItemMeta(meta);
				return new ItemDrop(item);
			}
			case LUCKY_BLOCK_ITEM: {
				int i = 1;
				try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
				if(i < 1) i = 1;
				if(sp[1].equalsIgnoreCase("random")) return new RandomLuckyItemDrop(i);
				return new LuckyItemDrop(LuckyBlockType.valueOf(sp[1].toUpperCase()), i);
			}
			case CUSTOM_ITEM: {
				int i = 1;
				try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
				if(i < 1) i = 1;
				ItemStack stack = CustomItemFactory.fetchCustomItem(sp[1].contains("-") ? sp[1] : "ntdluckyblock-" + sp[1]).clone();
				stack.setAmount(i);
				return new ItemDrop(stack);
			}
			case SCHEMATIC: {
				
				if(!LBMain.getIsSk89q()) {
					LBMain.getInstance().getLogger().log(Level.WARNING, "WorldEdit or WorldGuard was not found, lucky item \""
							+ drop + "\" wont be loaded");
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
				case EXPERIENCE_EXPLOSION: {
					int i = special.defaultValue();
					try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
					if(i < 1) i = special.defaultValue();
					return new ExperienceExplosionSpecial(i);
				}
				case TNT_EXPLOSION: {
					int i = special.defaultValue();
					try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
					if(i < 1) i = special.defaultValue();
					return new TntExplosionSpecial(i);
				}
				case TNT_COLUMN: {
					int i = special.defaultValue();
					try { i = Integer.parseInt(sp[2]); } catch(Exception ignored) {}
					if(i < 1) i = special.defaultValue();
					return new TntColumnSpecial(i);
				}
				default:
					break;
				}
				
			}
			default: return null;
			}
			
		} catch(Exception ignored) {
			if(LBMain.isDebug()) {
				LBMain.log(Level.WARNING, "Exception while loading \"" + drop + " \"");
				ignored.printStackTrace();
			}
		}
		return null;
		
	}
	
}
