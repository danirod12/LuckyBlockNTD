package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class VersionCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return false; }
	
	@Override
	public CommandResponce execute(CommandSender sender, String label, String[] args) {
		
		sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fThis server is running \u00a7eLuckyBlock NTD");
		sender.sendMessage("\u00a78 • \u00a7fRunning version - \u00a7e" + LBMain.getVersion() + " \u00a77(" +
				(LBMain.isPremium() ? "\u00a7bprem" : "\u00a7afree") + "\u00a77) " + (LBMain.getUpdater().need_update$cache() ?
						"\u00a7c\u00a7l[!] \u00a7cVersion " + LBMain.getInstance().updater.getLatestVersion() + " available" : "\u00a7aLatest version"));
		sender.sendMessage("\u00a78 • \u00a7fBuild - \u00a7e" + LBMain.getBuild() + "\u00a7f, last update - \u00a7e" + LBMain.getLastUpdate());
		sender.sendMessage("\u00a78 • \u00a7fPlugin author - \u00a7eDenBeKKer \u00a77(Known as danirod12)");
		
		for(String arg : args) {
			
			if(arg.equalsIgnoreCase("-full") || arg.equalsIgnoreCase("-f")) {
				
				// premium download info
				
				sender.sendMessage("\u00a78 • \u00a7fPlatform - \u00a7e" + Bukkit.getVersion() + " \u00a77(" + LBMain.getNMSVersion() + ")");
//				sender.sendMessage("\u00a78 • \u00a7fHooks - \u00a7e"
//						+ Misc.toString(Stream.of(Hooks.values()).filter(n -> n.isEnabled()).map(n -> n.name()).collect(Collectors.toList()), false));
//				Seems like it not allowed by SpigotMC rules
				
			}
			
		}
		
		sender.sendMessage("\u00a78 • \u00a7fSpigotMC url - \u00a7b" + LBMain.getUpdater().getResourceURL());
		sender.sendMessage("\u00a78 • \u00a7fbStats metrics - \u00a7bhttps://clck.ru/X9Z6e");
		return CommandResponce.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "version", "ver", "v", "build", "debug" };
	}
	
	@Override
	public Message helpMessage() { return null; }
	
}
