package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdatesCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(CommandSender sender, String label, String[] args) {
		
		sender.sendMessage("\u00a7aChecking for updates...");
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				LBMain.checkForUpdates(true);
				if(!LBMain.getUpdater().need_update$cache()) {
					sender.sendMessage("\u00a7aNo updates were found :(");
					if(!LBMain.isPremium())
						sender.sendMessage("\u00a7bWant more features? Check premium version!");
				}
				
			}
			
		}.runTaskAsynchronously(LBMain.getInstance());
		return CommandResponce.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "checkforupdates", "updates" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_CHECKFORUPDATES; }
	
}
