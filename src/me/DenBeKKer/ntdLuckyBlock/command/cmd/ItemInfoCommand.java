package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;

public class ItemInfoCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(Player player, String label, String[] args) {
		
		ItemStack item = LBMain.getInstance().factory.getItemInMainHand(player);
		
		if(item == null) {
			player.sendMessage("\u00a7cTake item in hand for this command");
			return CommandResponce.SUCCESS;
		}
		
		BekkerItemStack stack = CustomItemFactory.fetchCustomItem(item);
		if(stack != null) {
			player.sendMessage("\u00a7eCustom item. Identifier - \u00a76" + stack.getIdentifier().toString());
			return CommandResponce.SUCCESS;
		}
		
		LuckyBlockType name = LuckyBlockAPI.getLuckyBlock(item, true, false);
		LuckyBlockType uuid = LuckyBlockAPI.getLuckyBlock(item, false, true);
		LuckyBlockType config = LuckyBlockAPI.getLuckyBlock(item, LBMain.isVerifyName(), LBMain.isVerifyUUID());
		
		if(name == null && uuid == null) {
			player.sendMessage("\u00a7cItem type - \u00a7e" + item.getType());
			return CommandResponce.SUCCESS;
		}
		
		if(name != null) {
			player.sendMessage("\u00a7cItem name \"\u00a7f" + item.getItemMeta().getDisplayName().replace("\u00a7", "&")
					+ "\u00a7c\" matches with \u00a7" + name.toColorSymbol() + name.name() + "\u00a7c luckyblock");
		}
		if(uuid != null) {
			player.sendMessage("\u00a7cItem UUID \"\u00a7f" + LBMain.getUUID(item)
					+ "\u00a7c\" matches with \u00a7" + uuid.toColorSymbol() + uuid.name() + "\u00a7c luckyblock");
		}
		
		if(config != null) {
			player.sendMessage("\u00a7cThis items matches with \u00a7" + config.toColorSymbol() + config.name() + "\u00a7c by config setup");
			if(!config.isLoaded()) {
				player.sendMessage("\u00a7cLuckyBlock for \u00a7" + config.toColorSymbol() + config.name() + " \u00a7cnot loaded");
			} else {
				LuckyBlock block;
				try {
					block = config.get();
				} catch (LuckyBlockNotLoadedException e) {
					e.printStackTrace();
					return CommandResponce.SUCCESS;
				}
				player.sendMessage("\u00a78 > \u00a7fEconomy enabled - \u00a7e" + block.canBeShoped()
					+ (block.canBeShoped() ? "\u00a77 (Price: " + block.getPrice() + ")" : ""));
				player.sendMessage("\u00a78 > \u00a7fCrafts enabled - \u00a7e" + block.getRecipes().size());
				player.sendMessage("\u00a78 > \u00a7fCustom name - \u00a7e" + block.getCustomName());
				player.sendMessage("\u00a78 > \u00a7fAvailable drops - \u00a7e" + block.items$mapped() + "/" + block.items$mappedTwice());
			}
		}
		player.sendMessage("\u00a7cItem type - \u00a7e" + item.getType());
		return CommandResponce.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "iteminfo", "ii" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_ITEMINFO; }
	
}
