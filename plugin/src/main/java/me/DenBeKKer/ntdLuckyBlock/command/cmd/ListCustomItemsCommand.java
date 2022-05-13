package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class ListCustomItemsCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponse execute(CommandSender sender, String label, String[] args) {
		
		sender.sendMessage(Message.CI_LIST.getAsString(true));
		
		Collection<BekkerItemStack> collection = CustomItemFactory.copy();
		if(collection.size() == 0)
			sender.sendMessage("\u00a77 >>\u00a7c List is empty");
		else for(BekkerItemStack stack : collection) {
			final String plugin = stack.getIdentifier().getIdentifier().split("-")[0];
			sender.sendMessage("\u00a78 > \u00a7e" + stack.getIdentifier().getIdentifier()
					+ " \u00a77(" + (plugin.equalsIgnoreCase(LBMain.getInstance().getName()) ? "\u00a76" : "\u00a7e") + plugin + "\u00a77)");
		}
		return CommandResponse.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "customitemslist", "listcustomitems", "lci", "cil" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_CUSTOMITEMSLIST; }
	
}
