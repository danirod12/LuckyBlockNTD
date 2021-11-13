package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class ReloadCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() {
		return false;
	}
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(CommandSender sender, String label, String[] args) {
		
		final long ms = System.currentTimeMillis();
		
		LBMain.getInstance().loadConfig();
		LBMain.getInstance().system_load();
		try {
			GuiManager.init();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		sender.sendMessage(Message.RELOADED_CONFIG.getAsString().replace("%amount%", String.valueOf(LuckyBlockType.map().size())));
		LBMain.log(Level.INFO, "Reloaded (took " + (System.currentTimeMillis() - ms) + " ms)... ");
		return CommandResponce.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "reload", "restart", "reboot" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_RELOAD; }
	
}
