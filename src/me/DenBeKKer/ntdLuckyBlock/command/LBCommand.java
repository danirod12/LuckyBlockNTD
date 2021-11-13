package me.DenBeKKer.ntdLuckyBlock.command;

import org.bukkit.command.CommandSender;

import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public interface LBCommand {
	
	boolean onlyPlayer();
	
	boolean permission();
	
	CommandResponce execute(CommandSender sender, String label, String args[]);
	
	String[] commands();
	
	Message helpMessage();
	
}
