package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.command.CommandSender;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;

public class ListCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(CommandSender sender, String label, String[] args) {
		
		Collection<LuckyBlockType> disabled = new ArrayList<>();
		for(LuckyBlockType type : LuckyBlockType.values()) {
			
			if(!type.isLoaded()) {
				disabled.add(type);
				continue;
			}
			LuckyBlock block = LuckyBlockType.map().get(type);
			sender.sendMessage("\u00a78 • \u00a7" + type.toColorSymbol() + type.name() + " \u00a7aenabled \u00a77(Entries: " +
					block.items$mapped() + ", Recipes: " + block.getRecipes().size() + ")");
			
		}
		disabled.forEach(n -> sender.sendMessage("\u00a78 • \u00a7" + n.toColorSymbol() + n.name() + " \u00a7cdisabled"));
		return CommandResponce.SUCCESS;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "list", "luckyblocks" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_LIST; }
	
}
