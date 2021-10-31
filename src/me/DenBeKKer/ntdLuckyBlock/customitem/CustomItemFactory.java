package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
		
		if(custom_items.get().getBoolean("magic-wool.enabled")) {
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
	
}
