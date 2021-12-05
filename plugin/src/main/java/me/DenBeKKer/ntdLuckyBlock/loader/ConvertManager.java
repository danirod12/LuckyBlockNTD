package me.DenBeKKer.ntdLuckyBlock.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.util.Config;

public class ConvertManager implements Listener {
	
	private HashMap<Config, Collection<String>> map = new HashMap<>();
	private boolean verify_uuid, verify_tag;
	private boolean factory;
	
	public void add(Config loaded, String path) {
		
		Collection<String> collection = map.containsKey(loaded) ? map.get(loaded) : new ArrayList<>();
		collection.add(path);
		map.put(loaded, collection);
		
	}
	
	public int getRequests() {
		int i = 0;
		for(Collection<String> collection : map.values())
			i += collection.size();
		return i;
	}
	
	public HashMap<Config, Collection<String>> getRequestMap() {
		return map;
	}
	
	public boolean isVerifyUUID() {
		return verify_uuid;
	}
	
	public boolean isVerifyTAG() {
		return verify_tag;
	}
	
	public void options(boolean verify_uuid, boolean verify_tag) {
		this.verify_uuid = verify_uuid;
		this.verify_tag = verify_tag;
	}
	
	public void toggleFactory(boolean factory) {
		this.factory = factory;
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) { if(factory) convert(e.getPlayer()); }
	
	@EventHandler
	public void inventory(InventoryClickEvent e) {
		
		if(!factory) return;
		
		if(e.getInventory().getType() != InventoryType.PLAYER) {
			
			if(e.getCurrentItem() != null) {
				
				LuckyBlockType type = LuckyBlockAPI.parseOldLuckyBlock(e.getCurrentItem());
				if(type != null) {
					
					int amount = e.getCurrentItem().getAmount();
					ItemStack stack = type.isLoaded() ? LuckyBlockType.map().get(type).getSkull() : null;
					if(stack != null) stack.setAmount(amount);
					e.setCurrentItem(stack);
					LBMain.debug("Converted (" + type.name() + ", " + amount + ") for " + e.getWhoClicked().getName());
					
				}
				
			}
			
		}
		
	}
	
	public static void convert(Player player) {
		
		LBMain.debug("Converting " + player.getName() + "...");
		final PlayerInventory inventory = player.getInventory();
		for(int slot = 0; slot < 36; slot++) {
			
			ItemStack stack = inventory.getItem(slot);
			if(stack == null) continue;
			
			LuckyBlockType type = LuckyBlockAPI.parseOldLuckyBlock(stack);
			if(type == null) continue;
			
			int amount = stack.getAmount();
			stack = type.isLoaded() ? LuckyBlockType.map().get(type).getSkull() : null;
			if(stack != null) stack.setAmount(amount);
			inventory.setItem(slot, stack);
			LBMain.debug("Converted (" + type.name() + ", " + amount + ") for " + player.getName());
			
		}

	}
	
	public boolean isFactory() { return factory; }
	
}
