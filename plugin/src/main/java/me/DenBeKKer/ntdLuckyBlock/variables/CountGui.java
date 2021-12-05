package me.DenBeKKer.ntdLuckyBlock.variables;

import java.util.Arrays;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.DenBeKKer.ntdLuckyBlock.LBMain.PlayerHead;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class CountGui implements Listener {
	
	private Player player;
	private int start;
	private int limit;
	private int offset;
	private ConfirmEvent event;
	
	private Inventory inventory;
	private ItemStack bag;
	
	private int current;
	private boolean eco;
	private double price;
	
	public CountGui(Player p, int start, int limit, int offset, ConfirmEvent event, ItemStack bag, boolean eco, double price_per_chart) {
		
		this.player = p;
		this.start = current = start;
		this.limit = limit;
		this.offset = offset;
		this.event = event;
		this.bag = bag.clone();
		this.eco = eco;
		this.price = price_per_chart;
		this.inventory = Bukkit.createInventory(player, 27, Message.GUI_COUNT_TITLE.getAsString());
		inventory.setItem(18, PlayerHead.PREVIOUS_WOOD.getHead(Message.GUI_COUNT_BACK.getAsString(), null));
		inventory.setItem(26, PlayerHead.NEXT_WOOD.getHead(Message.GUI_COUNT_CONFIRM.getAsString(), null));
		i();
		
		Bukkit.getPluginManager().registerEvents(this, LBMain.getInstance());
		p.openInventory(inventory);
		
	}
	
	private void i() {
		
		if(current > offset)
			inventory.setItem(11, PlayerHead.MINUS_WOOD.getHead(Message.GUI_COUNT_REMOVE.getAsString().replace("%offset%", String.valueOf(offset)), null));
		else
			inventory.setItem(11, PlayerHead.MINUS_STONE.getHead(Message.GUI_COUNT_REMOVE.getAsString().replace("%offset%", String.valueOf(offset)), null));
		if(current < limit)
			inventory.setItem(15, PlayerHead.PLUS_WOOD.getHead(Message.GUI_COUNT_ADD.getAsString().replace("%offset%", String.valueOf(offset)), null));
		else
			inventory.setItem(15, PlayerHead.PLUS_STONE.getHead(Message.GUI_COUNT_ADD.getAsString().replace("%offset%", String.valueOf(offset)), null));
		
		b();
		inventory.setItem(13, bag);
		
	}
	
	private void b() {
		
		ItemMeta meta = bag.getItemMeta();
//		meta.setLore(Arrays.asList("\u00a7f ", "\u00a7fYou will get: \u00a76" + current, (eco ? ("\u00a7fYou will give: \u00a7c" + getPrice()) : "")));
		meta.setLore(Arrays.asList("\u00a7f ", Message.GUI_COUNT_GET.getAsString().replace("%amount%", String.valueOf(current)),
				eco ? (Message.GUI_COUNT_GIVE.getAsString().replace("%price%", String.valueOf(getPrice()))) : ""));
		bag.setItemMeta(meta);
		
	}
	
	private String getPrice() { return LBMain.getEconomy().format((int) (current * price)); }
	
	@EventHandler
	public void close(InventoryCloseEvent e) {
		if(e.getInventory().equals(inventory)) event.onConfirm(-54);
	}
	
	@EventHandler
	public void click(InventoryClickEvent e) {
		
		if(e.getSlot() < 0) return;
		if(e.getClickedInventory().equals(inventory)) {
			
			e.setCancelled(true);
			ItemStack item = e.getInventory().getItem(e.getSlot());
			if(item == null || !LBMain.getInstance().factory.isSkull(item)) return;
			
			switch(e.getSlot()) {
			case 18: {
				if(LBMain.isDebug()) LBMain.debug("back");
				event.goBack();
				return;
			}
			case 26: {
				if(LBMain.isDebug()) LBMain.debug("confirm");
				event.onConfirm(current);
				return;
			}
			case 11: {
				if(LBMain.isDebug()) LBMain.debug("minus");
				current -= offset;
				if(current < start) current = start;
				b(); i();
				return;
			}
			case 15: {
				if(LBMain.isDebug()) LBMain.debug("plus");
				current += offset;
				if(current > limit) current = limit;
				b(); i();
				return;
			}
			}
			
		}
		
	}
	
}
