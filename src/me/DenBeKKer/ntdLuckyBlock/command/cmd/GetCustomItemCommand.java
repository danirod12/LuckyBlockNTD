package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class GetCustomItemCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(Player sender, String label, String[] args) {
		
		if(args.length > 0) {
			
			if(!args[0].contains("-"))
				args[0] = "ntdluckyblock-" + args[0];
			
			BekkerItemStack stack = CustomItemFactory.fetchCustomItem(args[0]);
			if(stack == null) {
				sender.sendMessage(Message.CI_NOT_FOUND.getAsString(true).replace("%identifier%", args[0]));
				return CommandResponce.SUCCESS;
			}
			sender.getInventory().addItem(stack);
			sender.sendMessage(Message.CMD_CI_RECEIVED.getAsString(true));
			return CommandResponce.SUCCESS;
			
		}
		return CommandResponce.SEND_HELP;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "customitemget", "getcustomitem", "cig", "gci" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_CUSTOMITEMGET; }
	
}
