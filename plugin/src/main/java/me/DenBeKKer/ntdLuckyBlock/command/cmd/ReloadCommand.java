package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class ReloadCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() {
		return false;
	}
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponse execute(CommandSender sender, String label, String[] args) {
		
		final long ms = System.currentTimeMillis();
		
		LBMain.getInstance().loadConfig();
		LBMain.getInstance().reloadSystem();
		LBMain.getInstance().gui_manager.reload();
		
		sender.sendMessage(Message.RELOADED_CONFIG.getAsString().replace("%amount%", String.valueOf(LuckyBlockType.map().size())));
		LBMain.log(Level.INFO, "Reloaded (took " + (System.currentTimeMillis() - ms) + " ms)... ");
		return CommandResponse.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "reload", "restart", "reboot" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_RELOAD; }
	
}
