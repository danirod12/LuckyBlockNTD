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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.DenBeKKer.ntdLuckyBlock.LBHandler;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;

public class CustomItemFactory {
	
	public static final String TAG_IDENTIFIER_NAME = "bekker_item_identifier";
	private static Collection<BekkerItemStack> storage;
	
	public static void register(BekkerItemStack item) {
		BekkerItemStack origin = fetchCustomItem(item);
		if(origin != null)
			storage.remove(origin);
		storage.add(item);
		LBMain.log(Level.INFO, "Registered a new custom item - " + item.getClass().getSimpleName() + " (" + item.getEvents().size() + " events)");
	}
	
	private static void register(BekkerItemStackBuilder builder) {
		if(!builder.getIdentifier().toString().split("-")[0].equalsIgnoreCase(LBMain.getInstance().getName())) {
			throw new UnsupportedOperationException("Only system items can be registered using this method.");
		}
		BekkerItemStack stack = builder.build();
		if(stack != null) storage.add(stack);
	}
	
	public static boolean compare(ItemStack item, String identifier) {
		
		final Object tag = Identifier.getTag(Identifier.asNMSCopy(item));
		if(tag == null) return false;
		
		final String id = Identifier.getTagString(tag, CustomItemFactory.TAG_IDENTIFIER_NAME);
		return id != null && id.equalsIgnoreCase(identifier);
		
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
	
	public static void loadSystem() {
		
		storage = new ArrayList<>();
		
		Config custom_items = new Config(LBMain.getInstance(), null, "custom_items");
		custom_items.copy(true);
		
		// check if new items missed
		
		if(isEnabled(custom_items, "magic_wool")) {
			register(new BekkerItemStackBuilder(LBMain.getInstance().factory.getItem(Mat.WHITE_WOOL, 1))
					.addUnsafeEnchantment(Enchantment.DURABILITY, 1).setSerialID("magic_wool")
					.hideEnchantments().setName(Message.CI_MAGIC_WOOL.getAsString(true))
					.registerEvent(ItemEvent.PLACE, new PlaceEvent() {
						
						@Override
						public void execute(BlockPlaceEvent e) {
							
							new BukkitRunnable() {
								
								int rounds = ThreadLocalRandom.current().nextInt(5, 15);
								final Block block = e.getBlock();
								
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
							
						}
						
					}));
		}
		if(isEnabled(custom_items, "sword_of_justice")) {
			double h = custom_items.get().getDouble("sword_of_justice.heal");
			if(h <= 0) h = 1;
			final double heal = h;
			register(new BekkerItemStackBuilder(Material.IRON_SWORD).addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
					.setSerialID("sword_of_justice").setName(Message.CI_SWORD_OF_JUSTICE.getAsString(true))
					.registerEvent(ItemEvent.HIT, new HitEvent() {
						
						@SuppressWarnings("deprecation")
						@Override
						public void execute(Entity a, Entity b, HitEvent.Type type) {
							
							if(type != HitEvent.Type.DAMAGER) return;
							final Player player = (Player) a;
							player.setHealth(Math.min(player.getMaxHealth(), heal + player.getHealth()));
							
						}
						
					}));
		}
		if(isEnabled(custom_items, "axe_of_perun")) {
			double d = custom_items.get().getDouble("axe_of_perun.damage");
			if(d <= 0) d = .1D;
			final double damage = d;
			register(new BekkerItemStackBuilder(Material.DIAMOND_AXE).addUnsafeEnchantment(Enchantment.DURABILITY, 1)
					.setSerialID("axe_of_perun").setName(Message.CI_AXE_OF_PERUN.getAsString(true))
					.registerEvent(ItemEvent.HIT, new HitEvent() {
						
						@Override
						public void execute(Entity a, Entity v, HitEvent.Type type) {
							
							if(type != HitEvent.Type.DAMAGER) return;
							LightningStrike strike = v.getWorld().strikeLightning(v.getLocation());
							LBHandler.register(strike, damage);
							
						}
						
					}));
		}
		if(isEnabled(custom_items, "carrot_corrupter")) {
			register(new BekkerItemStackBuilder(Material.CARROT).addUnsafeEnchantment(Enchantment.DURABILITY, 1)
					.hideEnchantments().setSerialID("carrot_corrupter").setName(Message.CI_CARROT_CORRUPTER.getAsString(true))
					.registerEvent(ItemEvent.HIT, new HitEvent() {
						
						@Override
						public void execute(Entity d, Entity v, HitEvent.Type type) {
							
							if(type != HitEvent.Type.DAMAGER) return;
							if(!(v instanceof Player)) return;
							
							final Player victim = (Player) v;
							List<Integer> slots = new ArrayList<>();
							for(int slot = 0; slot < 9; slot++)
								if(!isEmpty(victim.getInventory().getItem(slot))) slots.add(slot);
							if(slots.size() != 0) {
								victim.getInventory().setItem(slots.get(ThreadLocalRandom.current().nextInt(slots.size())),
										new ItemStack(Material.CARROT));
							} else {
								victim.getInventory().setItem(ThreadLocalRandom.current().nextInt(9), new ItemStack(Material.CARROT));
							}
							withdrawItem((Player) d);
							
						}
						
					}));
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
	
	public static void reloadSystem() {
		
		if(storage != null) {
			
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
