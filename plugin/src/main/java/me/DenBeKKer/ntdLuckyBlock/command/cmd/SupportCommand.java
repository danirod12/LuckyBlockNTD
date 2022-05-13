package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static me.DenBeKKer.ntdLuckyBlock.LBMain.getDiscordURL;

public class SupportCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponse execute(CommandSender sender, String label, String[] args) {
		
		LBMain.log(Level.INFO, "&f=-= &6SUPPORT & BUG REPORTING & FEATURE REQUESTING &f=-=");
		LBMain.log(Level.INFO, "\u00a78 > \u00a7bDiscord \u00a7f- " + getDiscordURL());
		LBMain.log(Level.INFO, "\u00a78 > \u00a79Vkontakte \u00a7f- https://vk.com/danirodplay \u00a77(Rus)");
		LBMain.log(Level.INFO, "\u00a78 > \u00a76API usage \u00a7f- https://clck.ru/V3DoJ");
		LBMain.log(Level.INFO, "\u00a78 > \u00a76LuckyBlock setup \u00a7f- https://clck.ru/VnqVN");
		LBMain.log(Level.INFO, "\u00a7f=-= \u00a76SUPPORT & BUG REPORTING & FEATURE REQUESTING \u00a7f=-=");
		if(sender instanceof Player) {
			sender.sendMessage("\u00a7f=-= \u00a76SUPPORT & BUG REPORTING & FEATURE REQUESTING \u00a7f=-=");
			sender.sendMessage("\u00a78 > \u00a7bDiscord \u00a7f- " + getDiscordURL());
			sender.sendMessage("\u00a78 > \u00a79Vkontakte \u00a7f- https://vk.com/danirodplay \u00a77(Rus)");
			sender.sendMessage("\u00a78 > \u00a76API usage \u00a7f- https://clck.ru/V3DoJ");
			sender.sendMessage("\u00a78 > \u00a76LuckyBlock setup \u00a7f- https://clck.ru/VnqVN");
			sender.sendMessage("\u00a7f=-= \u00a76SUPPORT & BUG REPORTING & FEATURE REQUESTING \u00a7f=-=");
		}
		return CommandResponse.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "support" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_SUPPORT; }
	
}
