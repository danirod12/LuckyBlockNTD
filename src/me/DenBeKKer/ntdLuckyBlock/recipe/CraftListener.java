package me.DenBeKKer.ntdLuckyBlock.recipe;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;

public class CraftListener implements Listener {
	
	@EventHandler
	public void craft(PrepareItemCraftEvent e) {
		
		CraftingInventory inventory = e.getInventory();
		Player player = null;
		try {
			player = (Player) e.getViewers().get(0);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		boolean debug = LBMain.isDebug();
		if(debug)
			LBMain.debug("PrepareItemCraftEvent, player - " + (player == null ? "null" : player.getName()));
		
		for(LuckyBlockType type : LuckyBlockType.map().keySet()) {
			
			for(LuckyRecipe recipe : type.getRecipes()) {
				
				if(recipe == null || !recipe.hasAccess(player)) {
					if(debug)
						LBMain.debug("No permission for craft");
					continue;
				}
				
				if(recipe.verify(inventory.getMatrix())) {
					if(debug)
						LBMain.debug("Inserting new craft result");
					inventory.setResult(recipe.getResult());
					return;
				}
				
			}
			
		}
		
	}
	
}
