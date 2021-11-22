package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.customitem.Identifier;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;

public class ItemInfoCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(Player player, String label, String[] args) {
		
		final ItemStack item = LBMain.getInstance().factory.getItemInMainHand(player);
		
		if(item == null) {
			player.sendMessage("\u00a7cTake item in hand for this command");
			return CommandResponce.SUCCESS;
		}
		
		final BekkerItemStack stack = CustomItemFactory.fetchCustomItem(item);
		if(stack != null) {
			player.sendMessage("\u00a7eCustom item. Identifier - \u00a76" + stack.getIdentifier().getIdentifier());
		} else {
			final LuckyBlockType old = LuckyBlockAPI.parseOldLuckyBlock(item);
			if(old != null) {
				player.sendMessage("\u00a7cOld unconverted LuckyBlock - \u00a7e" + old.name());
				return CommandResponce.SUCCESS;
			}
			
			final LuckyBlockType uuid = LuckyBlockAPI.parseLuckyBlock(item, false);
			final LuckyBlockType tag = LuckyBlockAPI.parseLuckyBlock(item);
			
			if(tag != null || uuid != null) {
				
				if(uuid != null) {
					player.sendMessage("\u00a7cItem UUID \"\u00a7f" + LBMain.getUUID(item)
							+ "\u00a7c\" matches with \u00a7" + uuid.getCustomName(true) + "\u00a7c luckyblock");
				}
				if(tag != null) {
					player.sendMessage("\u00a7cItem TAG matches with \u00a7" + tag.getCustomName(true) + "\u00a7c luckyblock");
				}
				
				if((uuid != null || !LBMain.getConvertManager().isVerifyUUID())
						&& (tag != null || !LBMain.getConvertManager().isVerifyTAG())) {
					player.sendMessage("\u00a7cThis items matches with \u00a7" + uuid.getCustomName(true) + "\u00a7c by config settings");
					if(!uuid.isLoaded()) {
						player.sendMessage("\u00a7cLuckyBlock for \u00a7" + uuid.getCustomName(true) + " \u00a7cnot loaded");
					} else {
						LuckyBlock block = LuckyBlockType.map().get(uuid);
						player.sendMessage("\u00a78 > \u00a7fEconomy enabled - \u00a7e" + block.canBeShoped()
							+ (block.canBeShoped() ? "\u00a77 (Price: " + block.getPrice() + ")" : ""));
						player.sendMessage("\u00a78 > \u00a7fCrafts enabled - \u00a7e" + block.getRecipes().size());
						player.sendMessage("\u00a78 > \u00a7fCustom name - \u00a7e" + block.getCustomName());
						player.sendMessage("\u00a78 > \u00a7fAvailable drops - \u00a7e" + block.items$mapped() + "/" + block.items$mappedTwice());
					}
				}
				
			}
		}
		
		player.sendMessage("\u00a7cItem type - \u00a7e" + item.getType());
		for(String arg : args) {
			if(arg.equalsIgnoreCase("-tag"))
				player.sendMessage(new Gson().toJson(Identifier.getTag(Identifier.asNMSCopy(item))));
		}
		return CommandResponce.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "iteminfo", "ii" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_ITEMINFO; }
	
}
