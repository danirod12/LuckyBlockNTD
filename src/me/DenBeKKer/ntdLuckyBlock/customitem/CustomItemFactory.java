package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;

public class CustomItemFactory {
	
	public static final String TAG_IDENTIFIER_NAME = "bekker_item_identifier";
	public static final String TAG_LUCKYBLOCK_TYPE = "luckyblock_type";
	private static Collection<BekkerItemStack> storage;
	
	public static void register(BekkerItemStack item) {
		BekkerItemStack origin = fetchCustomItem(item);
		if(origin != null)
			storage.remove(origin);
		storage.add(item);
		LBMain.log(Level.INFO, "Registered a new custom item - " + item.getClass().getSimpleName() + " (" + item.getEvents().size() + " events)");
	}
	
	private static void register(BekkerItemStackBuilder builder) {
		if(!builder.getIdentifier().getIdentifier().split("-")[0].equalsIgnoreCase(LBMain.getInstance().getName())) {
			throw new UnsupportedOperationException("Only system items can be registered using this method.");
		}
		BekkerItemStack stack = builder.build();
		if(stack != null) storage.add(stack);
	}
	
	public static boolean compare(ItemStack item, String identifier) {
		
		return compare(item, TAG_IDENTIFIER_NAME, identifier);
		
	}
	
	public static boolean compare(ItemStack item, String tag_name, String identifier) {
		
		final Object tag = Identifier.getTag(Identifier.asNMSCopy(item));
		if(tag == null) return false;
		
		final String id = Identifier.getTagString(tag, tag_name);
		return id != null && id.equalsIgnoreCase(identifier);
		
	}
	
	public static String parseValue(ItemStack item, String tag_name) {
		
		final Object tag = Identifier.getTag(Identifier.asNMSCopy(item));
		if(tag == null) return null;
		
		final String id = Identifier.getTagString(tag, tag_name);
		return id != null && id.isEmpty() ? null : id;
		
	}
	
	public static BekkerItemStack fetchCustomItem(ItemStack item) {
		
		final Object tag = Identifier.getTag(Identifier.asNMSCopy(item));
		if(tag == null) return null;
		
		final String identifier = Identifier.getTagString(tag, CustomItemFactory.TAG_IDENTIFIER_NAME);
		if(identifier == null) return null;
		
		for(BekkerItemStack stack : storage)
			if(stack.equals((Object) identifier)) return stack;
		return null;
		
	}
	
	@SuppressWarnings("deprecation")
	public static void loadSystem() throws Throwable {
		
		storage = new ArrayList<>();
		
		Config custom_items = new Config(LBMain.getInstance(), "configuration.other", null, "custom_items");
		custom_items.copy(true);
		
		// check if new items missed
		
		if(isEnabled(custom_items, "magic_wool")) {
			try {
				register(new BekkerItemStackBuilder(LBMain.getInstance().factory.getItem(Mat.WHITE_WOOL, 1))
						.addUnsafeEnchantment(Enchantment.DURABILITY, 1).setSerialID("magic_wool")
						.hideEnchantments().setName(Message.CI_MAGIC_WOOL.getAsString(true))
						.registerEvent(ItemEvent.PLACE, n -> {
							
							new BukkitRunnable() {
								
								int rounds = ThreadLocalRandom.current().nextInt(5, 15);
								final Block block = n.getBlock();
								
								@Override
								public void run() {
									
									if(rounds < 0 || !block.getType().name().contains("WOOL")) {
										cancel();
										return;
									}
									
									if(LBMain.getInstance().factory instanceof Mat1_13)
										block.setType(IMat.WOOLS.get(ThreadLocalRandom.current().nextInt(IMat.WOOLS.size())));
									else IMat.setData(block, (byte)ThreadLocalRandom.current().nextInt(16));
									
									rounds--;
									
								}
								
							}.runTaskTimer(LBMain.getInstance(), 2L, 5L);
							
						}));
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		if(isEnabled(custom_items, "sword_of_justice")) {
			try {
				double h = custom_items.get().getDouble("sword_of_justice.heal");
				if(h <= 0) h = 1;
				final double heal = h;
				register(new BekkerItemStackBuilder(Material.IRON_SWORD).addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
						.setSerialID("sword_of_justice").setName(Message.CI_SWORD_OF_JUSTICE.getAsString(true))
						.registerEvent(ItemEvent.HIT, n -> {
							if(n.getType() != HitEvent.Type.DAMAGER) return;
							final Player player = (Player) n.getDamager();
							player.setHealth(Math.min(player.getMaxHealth(), heal + player.getHealth()));
						}));
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		if(isEnabled(custom_items, "axe_of_perun")) {
			try {
				double d = custom_items.get().getDouble("axe_of_perun.damage");
				if(d <= 0) d = .1D;
				final double damage = d;
				register(new BekkerItemStackBuilder(Material.DIAMOND_AXE).addUnsafeEnchantment(Enchantment.DURABILITY, 1)
						.setSerialID("axe_of_perun").setName(Message.CI_AXE_OF_PERUN.getAsString(true))
						.registerEvent(ItemEvent.HIT, n -> {
							if(n.getType() != HitEvent.Type.DAMAGER || !(n.getVictim() instanceof Damageable)) return;
							n.getVictim().getWorld().strikeLightningEffect(n.getVictim().getLocation());
							((Damageable)n.getVictim()).damage(damage);
						}));
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		if(isEnabled(custom_items, "mystery_meat")) {
			try {
				register(new BekkerItemStackBuilder(LBMain.getInstance().factory.getItem(Mat.BEEF, 1).getType())
						.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
						.hideEnchantments().setSerialID("mystery_meat").setName(Message.CI_MYSTERY_MEAT.getAsString(true))
						.registerEvent(ItemEvent.CONSUME, n -> {
							
							final Player player = n.getPlayer();
							final PotionEffectType type = PotionEffectType.values()[ThreadLocalRandom.current().nextInt(PotionEffectType.values().length)];
							try {
								player.addPotionEffect(new PotionEffect(type, ThreadLocalRandom.current().nextInt(5, 45) * 20, 1));
							} catch(Exception ex) {
								LBMain.log(Level.WARNING, "Report to author - " + LBMain.getDiscordURL());
								LBMain.log(Level.WARNING, " > PotionEffectType - " + type + ", " + ex.getLocalizedMessage());
								if(LBMain.isDebug()) ex.printStackTrace();
							}
							
						}));
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		if(LBMain.getInstance().factory instanceof Mat1_13 && isEnabled(custom_items, "carrot_corrupter")) {
			try {
				register(new BekkerItemStackBuilder(Material.CARROT).addUnsafeEnchantment(Enchantment.DURABILITY, 1)
						.hideEnchantments().setSerialID("carrot_corrupter").setName(Message.CI_CARROT_CORRUPTER.getAsString(true))
						.registerEvent(ItemEvent.HIT, n -> {
							if(n.getType() != HitEvent.Type.DAMAGER) return;
							if(!(n.getVictim() instanceof Player)) return;
							
							final Player victim = (Player) n.getVictim();
							List<Integer> slots = new ArrayList<>();
							for(int slot = 0; slot < 9; slot++)
								if(!isEmpty(victim.getInventory().getItem(slot))) slots.add(slot);
							if(slots.size() != 0) {
								victim.getInventory().setItem(slots.get(ThreadLocalRandom.current().nextInt(slots.size())),
										new ItemStack(Material.CARROT));
							} else {
								victim.getInventory().setItem(ThreadLocalRandom.current().nextInt(9), new ItemStack(Material.CARROT));
							}
							withdrawItem((Player) n.getDamager());
						}));
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		
	}
	
	public static void withdrawItem(Player damager) {
		
		if(damager.getGameMode() == GameMode.CREATIVE) return;
		
		PlayerInventory i = damager.getInventory();
		ItemStack stack = i.getItem(i.getHeldItemSlot());
		if(stack.getAmount() > 1) {
			stack.setAmount(stack.getAmount() - 1);
			i.setItem(i.getHeldItemSlot(), stack);
		} else {
			i.setItem(i.getHeldItemSlot(), null);
		}
		
	}
	
	public static boolean isEmpty(ItemStack stack) {
		return stack == null || stack.getType() == Material.AIR;
	}
	
	private static boolean isEnabled(Config config, String path) {
		
		if(!config.get().isSet(path + ".enabled")) {
			config.get().set(path + ".enabled", true);
			
			switch(path.toLowerCase()) {
			case "sword_of_justice": {
				config.get().set(path + ".heal", 2.0D);
				break;
			}
			case "axe_of_perun": {
				config.get().set(path + ".damage", .5D);
				break;
			}
			default: break;
			}
			
			LBMain.log(Level.INFO, "A new custom item here (" + path + "). \u00a7aEnabling!");
			config.save();
		}
		return config.get().getBoolean(path + ".enabled");
		
	}
	
	public static void reloadSystem() throws Throwable {
		
		if(storage != null && LBMain.getInstance().factory != null) {
			
			new ArrayList<>(storage).stream().filter(item -> {
				
				String identifier = Identifier.getTagString(Identifier.getTag(Identifier.asNMSCopy(item)), CustomItemFactory.TAG_IDENTIFIER_NAME);
				return identifier.split("-")[0].equalsIgnoreCase(LBMain.getInstance().getName());
				
			}).forEach(n -> storage.remove(n));
			
			loadSystem();
			
		}
		
	}
	
	public static BekkerItemStack fetchCustomItem(String string) {
		for(BekkerItemStack stack : storage)
			if(stack.equals((Object) string)) return stack;
		return null;
	}
	
	public static Collection<BekkerItemStack> copy() {
		return new ArrayList<>(storage);
	}
	
}
