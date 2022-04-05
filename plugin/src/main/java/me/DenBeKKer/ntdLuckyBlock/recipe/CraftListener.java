package me.DenBeKKer.ntdLuckyBlock.recipe;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.events.PrepareLuckyBlockCraftEvent;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

public class CraftListener implements Listener {
	
	@EventHandler
	public void craft(PrepareItemCraftEvent e) throws LuckyBlockNotLoadedException {
		
		CraftingInventory inventory = e.getInventory();
		Player player = null;
		try {
			player = (Player) e.getViewers().get(0);
		} catch(Exception ex) {
			if(LBMain.isDebug())
				ex.printStackTrace();
			return;
		}
		
		LBMain.debug("PrepareItemCraftEvent, player - " + (player == null ? "null" : player.getName()));
		if(player == null) return;
		
		for(LuckyBlockType type : LuckyBlockType.map().keySet()) {
			
			for(LuckyRecipe recipe : type.getRecipes()) {
				
				if(recipe == null || !recipe.hasAccess(player)) {
					LBMain.debug("No permission for craft");
					continue;
				}
				
				if(recipe.verify(inventory.getMatrix())) {
					
					for(int i = 0; i < inventory.getMatrix().length; i++) {
						if(inventory.getMatrix()[i] != null
								&& inventory.getMatrix()[i].getAmount() > 1) {
							if(LBMain.isDebug())
								LBMain.debug("Matrix checking. Проверка, один предмет в каждом слоте или нет");
							player.sendMessage(Message.CRAFT_ALLOWED_ONE_LAYER.getAsString());
							player.closeInventory();
							return;
						}
					}

					PrepareLuckyBlockCraftEvent event = new PrepareLuckyBlockCraftEvent(player, inventory.getMatrix(), recipe);
					Bukkit.getPluginManager().callEvent(event);
					if(event.isCancelled()) return;

					LBMain.debug("Inserting new craft result");
					inventory.setResult(recipe.getResult());
					return;
				}
				
			}
			
		}
		
	}
	
}
