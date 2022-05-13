package me.DenBeKKer.ntdLuckyBlock.command;

import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

public interface LBCommand {
	
	boolean onlyPlayer();
	
	boolean permission();
	
	CommandResponse execute(CommandSender sender, String label, String args[]);
	
	String[] commands();
	
	Message helpMessage();
	
}
