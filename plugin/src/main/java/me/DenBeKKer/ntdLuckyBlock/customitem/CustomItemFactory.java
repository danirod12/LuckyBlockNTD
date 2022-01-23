package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;

import me.DenBeKKer.ntdLuckyBlock.api.events.CustomItemAddedEvent;
import me.DenBeKKer.ntdLuckyBlock.nms.ItemTag;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
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
	private static final Collection<BekkerItemStack> storage = new ArrayList<>();

	public static boolean chilly_pants = false, rage_armor = false;
	public static boolean solid;
	public static int rage_armor_percentage;

	public static void register(BekkerItemStack item) {
		BekkerItemStack origin = fetchCustomItem(item);
		if(origin != null)
			storage.remove(origin);
		storage.add(item);
		LBMain.log(Level.INFO, "Registered a new custom item - " + item.getClass().getSimpleName() + " (" + item.getEvents().size() + " events)");
		Bukkit.getPluginManager().callEvent(new CustomItemAddedEvent(item));
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

		if(item == null || tag_name == null || identifier == null) return false;

		ItemTag adapter = LBMain.getItemTagAdapter();
		final Object tag = adapter.getTag(adapter.asNMSCopy(item));
		if(tag == null) return false;
		
		final String id = adapter.getTagString(tag, tag_name);
		return id != null && id.equalsIgnoreCase(identifier);
		
	}
	
	public static String parseValue(ItemStack item, String tag_name) {

		ItemTag adapter = LBMain.getItemTagAdapter();
		final Object tag = adapter.getTag(adapter.asNMSCopy(item));
		if(tag == null) return null;
		
		final String id = adapter.getTagString(tag, tag_name);
		return id != null && id.isEmpty() ? null : id;
		
	}
	
	public static BekkerItemStack fetchCustomItem(ItemStack item) {

		ItemTag adapter = LBMain.getItemTagAdapter();
		final Object tag = adapter.getTag(adapter.asNMSCopy(item));
		if(tag == null) return null;
		
		final String identifier = adapter.getTagString(tag, CustomItemFactory.TAG_IDENTIFIER_NAME);
		if(identifier == null) return null;
		
		for(BekkerItemStack stack : storage)
			if(stack.equals((Object) identifier)) return stack;
		return null;
		
	}
	
	@SuppressWarnings("deprecation")
	public static void loadSystem() throws Throwable {
		
		storage.clear();
		
		Config custom_items = new Config(LBMain.getInstance(), "configuration.other", null, "custom_items");
		custom_items.copyMissedFields(added -> {
			if(added.endsWith(".enabled"))
				LBMain.log(Level.INFO, "A new custom item here (" + added.split("\\.")[0] + "). \u00a7aEnabling!");
		});
		
		// check if new items is missed
		if(custom_items.getBoolean("magic_wool.enabled")) {
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
		if(custom_items.getBoolean("sword_of_justice.enabled")) {
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
		if(custom_items.getBoolean("axe_of_perun.enabled")) {
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
		if(custom_items.getBoolean("mystery_meat.enabled")) {
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
		if(custom_items.getBoolean("chilly_pants.enabled")) {
			solid = custom_items.get().getBoolean("chilly_pants.only_solid");
			try {
				register(new BekkerItemStackBuilder(Material.LEATHER_LEGGINGS).setSerialID("chilly_pants").setName(Message.CI_CHILLY_PANTS.getAsString(true)));
				chilly_pants = true;
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		if(custom_items.getBoolean("rage_armor.enabled")) {
			rage_armor_percentage = custom_items.get().getInt("rage_armor.percentage");
			try {
				register(new BekkerItemStackBuilder(Material.LEATHER_LEGGINGS).setSerialID("rage_armor_leggings").setName(Message.CI_RAGE_ARMOR.getAsString(true)));
				register(new BekkerItemStackBuilder(Material.LEATHER_BOOTS).setSerialID("rage_armor_boots").setName(Message.CI_RAGE_ARMOR.getAsString(true)));
				register(new BekkerItemStackBuilder(Material.LEATHER_CHESTPLATE).setSerialID("rage_armor_chestplate").setName(Message.CI_RAGE_ARMOR.getAsString(true)));
				register(new BekkerItemStackBuilder(Material.LEATHER_HELMET).setSerialID("rage_armor_helmet").setName(Message.CI_RAGE_ARMOR.getAsString(true)));
				rage_armor = true;
			} catch(Throwable th) {
				th.printStackTrace();
			}
		}
		if(custom_items.getBoolean("wither_blast_rod.enabled")) {
			final float wither_skull_yield = custom_items.getFloat("wither_blast_rod.yield");
			try {
				register(new BekkerItemStackBuilder(Material.BLAZE_ROD).setSerialID("wither_blast_rod")
						.addUnsafeEnchantment(Enchantment.DURABILITY, 1).hideEnchantments()
						.setName(Message.CI_WITHER_BLAST_ROD.getAsString(true))
						.registerEvent(ItemEvent.INTERACT, event -> {

							withdrawItem(event.getPlayer());
							WitherSkull skull = event.getPlayer().launchProjectile(WitherSkull.class);
							skull.setYield(wither_skull_yield);

						}));
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
		if(LBMain.getInstance().factory instanceof Mat1_13 && custom_items.getBoolean("carrot_corrupter.enabled")) {
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
		LBMain.log(Level.INFO, "Loaded " + storage.size() + " internal custom items...");
		
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
	
	public static void reloadSystem() throws Throwable {

		ItemTag adapter = LBMain.getItemTagAdapter();
		if(storage != null && LBMain.getInstance().factory != null) {
			
			new ArrayList<>(storage).stream().filter(item -> {
				
				String identifier = adapter.getTagString(adapter.getTag(adapter.asNMSCopy(item)), CustomItemFactory.TAG_IDENTIFIER_NAME);
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

	public static boolean isRageArmor(Player player) {

		PlayerInventory i = player.getInventory();
		return compare(i.getHelmet(), "ntdluckyblock-rage_armor_helmet") && compare(i.getLeggings(), "ntdluckyblock-rage_armor_leggings") &&
				compare(i.getChestplate(), "ntdluckyblock-rage_armor_chestplate") && compare(i.getBoots(), "ntdluckyblock-rage_armor_boots");

	}

}
