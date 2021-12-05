package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.command.CommandSender;

import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class ConvertCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(CommandSender sender, String label, String[] args) {
		
		if(LBMain.getConvertManager().getRequests() == 0) {
			sender.sendMessage("\u00a7cThere is no convert requests");
			return CommandResponce.SUCCESS;
		}
		
		if(!LBMain.isPremium()) {
			sender.sendMessage("\u00a7cThis feature available only in premium plugin version");
			return CommandResponce.SUCCESS;
		}
		
		// premium me.DenBeKKer.ntdLuckyBlock.command implementation
		throw new UnsupportedOperationException("Command not implemented. Hack attempt?");
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "convert" };
	}
	
	@Override
	public Message helpMessage() { return null; }
	
}
