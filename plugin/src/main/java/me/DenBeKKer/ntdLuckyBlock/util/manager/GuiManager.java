package me.DenBeKKer.ntdLuckyBlock.util.manager;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.LBMain.PlayerHead;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.variables.ConfirmEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.CountGui;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class GuiManager implements Listener {

	public GuiManager() {

		reload();

	}

	public enum GuiType {

		EDIT, GET;

		public static GuiType parseGuiType(String name) {
			for(GuiType type : values())
				if(type.name().equalsIgnoreCase(name))
					return type; return null;
		}

	}

	private Inventory get = null;
	private final HashMap<Player, CountGui> map = new HashMap<>();

	@Deprecated
	public void open(GuiType type, Player player) {
		open(player, type);
	}

	public void open(Player player, GuiType type) {

		if(type == GuiType.GET) {
			player.openInventory(get);
			return;
		}
		player.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7c" + type.name()
				+ " gui feature is disabled or available only in premium version");
		
	}
	
	@EventHandler
	public void click(final InventoryClickEvent e) throws LuckyBlockNotLoadedException {
		
		if(e.getSlot() < 0) return;
		if(e.getClickedInventory().equals(get)) {
			e.setCancelled(true);
			
			if(e.getSlot() == e.getClickedInventory().getSize() - 9) {
				e.getWhoClicked().closeInventory();
				return;
			}
			
			ItemStack item = e.getInventory().getItem(e.getSlot());
			if(item == null || !LBMain.getInstance().factory.isSkull(item)) return;
			
			final Player player = (Player) e.getWhoClicked();
			List<LuckyBlockType> types = LuckyBlockType.enabled().stream()
					.filter(n -> {
						try {
							return n.get().getSkull().getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName());
						} catch (LuckyBlockNotLoadedException e1) {
							e1.printStackTrace();
							return false;
						}
					})
					.limit(1).collect(Collectors.toList());
			if(types.size() != 1) return;
			final LuckyBlockType type = types.get(0);
			
			if(LBMain.getInstance().config.get().getBoolean("permission-for-each-gui-get") &&
					!player.hasPermission("luckyblock.get." + type.name().toLowerCase())
					&& !player.hasPermission("luckyblock.get.*")) {
				player.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", type.get().getCustomName()));
				return;
			}
			
			final LuckyBlock block = type.get();
			map.put(player, new CountGui(player, 1, 64, 1, (new ConfirmEvent() {
				
				@Override
				public void onConfirm(int amount) {
					
					map.remove(player);
					
					if(amount < 0) return;
					
					if(!block.canBeSold()) {
						for(int i = 0; i < amount; i++)
							block.giveItem(player);
						player.closeInventory();
						return;
					}
					
					double current = LBMain.getEconomy().getBalance(player);
					double cost = block.getPrice() * amount;
					if(current < cost) {
						player.sendMessage(Message.GUI_GET_NOT_MONEY.getAsString().replace("%eco%", LBMain.getEconomy().format((int) (cost - current))));
						player.closeInventory();
					} else {
						if(LBMain.getEconomy().withdraw(player, (int) cost)) {
							for(int i = 0; i < amount; i++)
								block.giveItem(player);
							player.sendMessage(Message.GUI_GET_SUCCESS.getAsString().replace("%eco%", LBMain.getEconomy().format((int) cost))
									.replace("%amount%", String.valueOf(amount)));
							player.openInventory(get);
						} else {
							player.closeInventory();
						}
					}
					
				}
				
				@Override
				public void goBack() {
					map.remove(player);
					player.openInventory(e.getInventory());
				}
				
			}), item, block.canBeSold(), block.getPrice()));
		}
		
	}
	
	public void ramFix(Player p) {
		map.remove(p);
	}
	
	public void reload() {

		close();

		Collection<LuckyBlockType> types = LuckyBlockType.enabled().stream()
				.filter(n -> LuckyBlockType.map().get(n).canBeShoped())
				.sorted(Comparator.comparingInt(n -> n.asColor().getData()))
				.collect(Collectors.toList());
		LBMain.debug("[GUIMANAGER] Found " + types.size() + " types");
		
		int rows = types.size() == 0 ? 3 : (int) Math.ceil(((double)types.size()) / 5);
		get = Bukkit.createInventory(null, (2 + rows) * 9, Message.GUI_GET_TITLE.getAsString(true));
		ItemStack gray_pane = LBMain.getInstance().factory.getItem(Mat.GRAY_PANE, 1);
		
		for(int row = 0; row < rows + 2; row++) {
			get.setItem(row * 9, gray_pane);
			
			if(row - 1 == rows) {
				ItemMeta meta = gray_pane.getItemMeta();
				meta.setLore(Arrays.asList("\u00a7f ", "\u00a7fRunning \u00a7eLuckyBlock NTD v" + LBMain.getVersion() + " \u00a77("
					+ (LBMain.isPremium() ? "\u00a7dprem" : "\u00a7afree") + "\u00a77)",
					"\u00a7fby \u00a7aDenBeKKer \u00a77(Known as danirod12)"));
				gray_pane.setItemMeta(meta);
			}
			get.setItem(row * 9 + 8, gray_pane);
			
		}
		
		gray_pane = LBMain.getInstance().factory.getItem(Mat.BLACK_PANE, 1);
		
		int amount = 0, slot = 11;
		for(LuckyBlockType type : types) {
			
			try {
				get.setItem(slot, type.get().getSkull());
			} catch (LuckyBlockNotLoadedException e) {
				e.printStackTrace();
			}
			amount++; slot++;
			
			if(amount >= 5) {
				
				slot += 4;
				amount = 0;
				
			}
			
		}
		
		while(amount != 0 && amount < 5) {
			
			get.setItem(slot, gray_pane);
			amount++; slot++;
			
		}
		
		createExit(get);
		
	}
	
	public static void createExit(Inventory inventory) {
		inventory.setItem(inventory.getSize() - 9, PlayerHead.CLOSE_WOOD.getHead(Message.GUI_EXIT.getAsString(true), Collections.singletonList("\u00a7f ")));
	}
	
	public void close() {
		new ArrayList<>(map.keySet()).forEach(HumanEntity::closeInventory);
		map.clear();
		if(get != null) new ArrayList<>(get.getViewers()).forEach(HumanEntity::closeInventory);
	}

}
