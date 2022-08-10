package me.DenBeKKer.ntdLuckyBlock.loader;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.loader.StringLoader;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
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
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class LegacyLoader implements StringLoader {
	
	public LuckyDrop load(String drop) {
		
		try {
			
			String[] sp = drop.split(" : ");
			LuckyItemType type = LuckyItemType.valueOf(sp[0].toUpperCase());
			
			switch(type) {
				case COMMAND: return new CommandDrop(sp[1]);
				case CONSOLE: return new ConsoleDrop(sp[1]);
				case OPPED: return new OppedDrop(sp[1]);
				case MESSAGE: return new MessageDrop(sp[1]);
				case ENTITY: {
					int i = 1;
					try {
						i = Integer.parseInt(sp[2]);
					} catch(Exception ignored) {}
					if(i < 1) i = 1;
					return new EntityDrop(EntityType.valueOf(sp[1].toUpperCase()), i);
				}
				case ITEM: {

					ItemStack item = new ItemStack(Material.valueOf(sp[1].toUpperCase()),
							Integer.parseInt(sp[2]), (short) Integer.parseInt(sp[3]));
					ItemMeta meta = item.getItemMeta();
					assert meta != null;

					if(sp.length >= 5) meta.setDisplayName(Misc.setColors(sp[4]));
					for(int index = 5; index < sp.length - 1; index += 2) {
						try {
							meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(sp[index].toUpperCase())),
									Integer.parseInt(sp[index + 1]), true);
						} catch(Exception ex) {
							ex.printStackTrace();
							LBMain.log(Level.WARNING, "Enchantment " + sp[index].toUpperCase() + " (level: " + sp[index + 1]
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
					BekkerItemStack item = CustomItemFactory.fetchCustomItem(sp[1].contains("-") ? sp[1] : "ntdluckyblock-" + sp[1]);
					if(item == null)
						throw new IllegalArgumentException("BekkerItemStack for name " + sp[1] + " was not found");
					ItemStack stack = item.clone();
					stack.setAmount(i);
					return new ItemDrop(stack);
				}
				case SCHEMATIC: {

                    if (!Hook.WorldEdit.isEnabled()) {
                        MvLogger.log(Level.WARNING, "WorldEdit was not found, lucky item \""
                                + drop + "\" wont be loaded");
                        return null;
                    }

                    boolean b;
                    if (sp[2].equalsIgnoreCase("block")) {
                        b = true;
                    } else if (sp[2].equalsIgnoreCase("player")) {
                        b = false;
                    } else throw new IllegalArgumentException("Schematic format allow only player and block arguments");

					String file$name = sp[1].endsWith(".schem") ? sp[1] : sp[1] + ".schem";

					File file = new File(LBMain.getSchematicsFolder(), file$name);
					if(!file.exists()) {

						file$name = sp[1].endsWith(".schematic") ? sp[1] : sp[1] + ".schematic";
						file = new File(LBMain.getSchematicsFolder(), file$name);

						if(!file.exists()) {
							if(LBMain.isDebug())
								LBMain.getInstance().getLogger().log(Level.WARNING, "Schematic " + file.getPath() + " not found");
							return null;
						}

					}

					return new SchematicDrop(file, b);

				}
				case SPECIAL: {

					Special special = Special.valueOf(sp[1].toUpperCase());

					switch(special) {
					case DIAMOND_COLUMN: {
						Collection<Material> collection = new ArrayList<>();
						for(int i = 2; i < sp.length; i++) {
							Material mat;
							try {
								mat = Material.valueOf(sp[i].toUpperCase());
							} catch(Exception ex) {
								LBMain.getInstance().getLogger().log(Level.WARNING, "Cant recognize material " + sp[i]);
								continue;
							}
							collection.add(mat);
						}
						return new DiamondColumnSpecial(collection);
					}
					case LIGHTNING: return new LightningSpecial(apply(special, sp[2]));
					case PIG: return new PigSpecial(apply(special, sp[2]));
					case WATER_BUCKET: return new WaterBucketSpecial(apply(special, sp[2]));
					case EXPERIENCE_EXPLOSION: return new ExperienceExplosionSpecial(apply(special, sp[2]));
					case TNT_EXPLOSION: return new TntExplosionSpecial(apply(special, sp[2]));
					case TNT_COLUMN: return new TntColumnSpecial(apply(special, sp[2]));
					default:
						return null;
					}

				}
				default: return null;
			}
			
		} catch(Throwable throwable) {
			if(LBMain.isDebug()) {
				LBMain.log(Level.WARNING, "An exception occurred while loading \"" + drop + " \"");
				throwable.printStackTrace();
			}
		}
		return null;
		
	}

	public int apply(Special special, String value) {

		final int i;
		try {
			i = Integer.parseInt(value);
		} catch(Exception ignored) {
			return special.defaultValue();
		}
		return i < 1 ? special.defaultValue() : i;

	}

	public String save(LuckyDrop drop) {
		if (drop instanceof CommandDrop) {
			return "COMMAND : " + ((CommandDrop) drop).getCommand();
		} else if (drop instanceof ConsoleDrop) {
			return "CONSOLE : " + ((ConsoleDrop) drop).getCommand();
		} else if (drop instanceof EntityDrop) {
			return "ENTITY : " + ((EntityDrop) drop).getEntityType() + " : " + ((EntityDrop) drop).getAmount();
		} else if (drop instanceof ItemDrop) {
			ItemStack stack = ((ItemDrop) drop).getItemCopy();
			StringBuilder item = new StringBuilder("ITEM : " + stack.getType().name() + " : "
					+ stack.getAmount() + " : " + stack.getDurability());
			ItemMeta meta = stack.getItemMeta();
			if (meta == null) return item.toString();
			item.append(" : ").append(meta.getDisplayName());
			for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
				item.append(" : ").append(entry.getKey().getName()).append(" : ").append(entry.getValue());
			}
			return item.toString();
		} else if (drop instanceof LuckyItemDrop) {
			return "LUCKY_BLOCK_ITEM : " + ((LuckyItemDrop) drop).getType() + " : " + ((LuckyItemDrop) drop).getAmount();
		} else if (drop instanceof MessageDrop) {
			return "MESSAGE : " + ((MessageDrop) drop).getMessage();
		} else if (drop instanceof OppedDrop) {
			return "OPPED : " + ((OppedDrop) drop).getCommand();
		} else if (drop instanceof  RandomLuckyItemDrop) {
			return "LUCKY_BLOCK_ITEM : RANDOM : " + ((RandomLuckyItemDrop) drop).getAmount();
		} else if (drop instanceof SchematicDrop) {
			return "SCHEMATIC : " + ((SchematicDrop) drop).getFile().getName() + " : "
					+ (((SchematicDrop) drop).atBlock() ? "BLOCK" : "PLAYER");
		} else if (drop instanceof WaterBucketSpecial) {
			return "SPECIAL : WATER_BUCKET : " + ((WaterBucketSpecial) drop).getHeight();
		} else if (drop instanceof TntExplosionSpecial) {
			return "SPECIAL : TNT_EXPLOSION : " + ((TntExplosionSpecial) drop).getAmount();
		} else if (drop instanceof TntColumnSpecial) {
			return "SPECIAL : TNT_COLUMN : " + ((TntColumnSpecial) drop).getAmount();
		} else if (drop instanceof PigSpecial) {
			return "SPECIAL : PIG " + ((PigSpecial) drop).getAmount();
		} else if (drop instanceof LightningSpecial) {
			return "SPECIAL : LIGHTNING : " + ((LightningSpecial) drop).getAmount();
		} else if (drop instanceof ExperienceExplosionSpecial) {
			return "SPECIAL : EXPERIENCE_EXPLOSION : " + ((ExperienceExplosionSpecial) drop).getAmount();
		}
		return null;
	}

}
