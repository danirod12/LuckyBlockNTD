package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class GiveCommand implements LBCommand {
	
	@Override
	public boolean onlyPlayer() { return false; }
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public CommandResponce execute(CommandSender sender, String label, String[] args) {
		
		if(args.length > 1) {
			
			Player p = null;
			byte data = 0;
			
			if(args[0].equalsIgnoreCase("all")) {
				data = 1;
			} else if(args[0].equalsIgnoreCase("world")) {
				data = 2;
			} else {
				p = Bukkit.getPlayerExact(args[0]);
				if(p == null) {
					sender.sendMessage(Message.CMD_PLAYER_NOT_FOUND.getAsString().replace("%player%", args[0]));
					return CommandResponce.SUCCESS;
				}
			}
			
			boolean random = false;
			LuckyBlockType type = null;
			if(args[1].equalsIgnoreCase("RANDOM")) {
				
				if(sender instanceof Player && !Misc.hasPermission((Player) sender, "luckyblock.command.give.random")) {
					sender.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", "RANDOM"));
					return CommandResponce.SUCCESS;
				}
				type = LuckyBlockType.random(true);
				random = true;
			}
			
			if(type == null) try {
				type = LuckyBlockType.valueOf(args[1].toUpperCase());
			} catch(Exception ex) {
				sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[1]));
				return CommandResponce.SUCCESS;
			}
			
			if(type == null) {
				sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[1]));
				return CommandResponce.SUCCESS;
			}
			if(!type.isLoaded()) {
				sender.sendMessage(Message.CMD_LB_DISABLED.getAsString().replace("%lb%", args[1]));
				return CommandResponce.SUCCESS;
			}
			
			if(!random && sender instanceof Player && LBMain.getInstance().config.get().getBoolean("permission-for-each-give-get") &&
					!Misc.hasPermission((Player) sender, "luckyblock.command.give." + type.name())) {
				sender.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", LuckyBlockType.map().get(type).getCustomName()));
				return CommandResponce.SUCCESS;
			}
			
			int amount = 1;
			try {
				amount = Integer.parseInt(args[2]);
			} catch(Exception ignored) {}
			if(amount < 1) amount = 1;
			
			ItemStack item = LuckyBlockType.map().get(type).getSkull();
			item.setAmount(amount);
			switch(data) {
			case 0: {
				p.getInventory().addItem(item);
				break;
			}
			case 1: {
				Bukkit.getOnlinePlayers().forEach(n -> n.getInventory().addItem(item));
				break;
			}
			case 2: {
				
				if(sender instanceof Player) {
					((Player)sender).getWorld().getPlayers().forEach(n -> n.getInventory().addItem(item));
				} else {
					sender.sendMessage("Argument world only for players ;(");
					return CommandResponce.SUCCESS;
				}
				
				break;
			}
			default:
				throw new IllegalArgumentException("Byte give type " + data + " not defined");
			}
			sender.sendMessage(Message.CMD_LB_GIVE.getAsString().replace("%lb%", type.getCustomName()).replace("%amount%", String.valueOf(amount)));
			return CommandResponce.SUCCESS;
			
		}
		return CommandResponce.SEND_HELP;
		
	}
	
	@Override
	public final String[] commands() {
		return new String[] { "give" };
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_GIVE; }
	
}
