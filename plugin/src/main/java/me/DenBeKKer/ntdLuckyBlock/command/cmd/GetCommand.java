package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class GetCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public final String[] commands() {
		return new String[] { "get" };
	}
	
	@Override
	public CommandResponce execute(Player player, String label, String[] args) {
		
		if(args.length > 0) {
			
			boolean random = false;
			LuckyBlockType type = null;
			if(args[0].equalsIgnoreCase("random")) {
				List<LuckyBlockType> types = Stream.of(LuckyBlockType.values()).filter(n -> n.isLoaded())
						.filter(n -> {
							
							return !LBMain.getInstance().config.get().getBoolean("permission-for-each-give-get") ||
									Misc.hasPermission(player, "luckyblock.command.get." + n.name());
							
						}).collect(Collectors.toList());
				if(types.size() == 0) {
					player.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", "RANDOM"));
					return CommandResponce.SUCCESS;
				}
				type = types.get(ThreadLocalRandom.current().nextInt(types.size()));
				random = true;
			}
			
			if(type == null) try {
				
				type = LuckyBlockType.valueOf(args[0].toUpperCase());
				
			} catch(Exception ex) {
				player.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[0]));
				return CommandResponce.SUCCESS;
			}
			
			if(type == null) {
				player.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[0]));
				return CommandResponce.SUCCESS;
			}
			if(!type.isLoaded()) {
				player.sendMessage(Message.CMD_LB_DISABLED.getAsString().replace("%lb%", args[0]));
				return CommandResponce.SUCCESS;
			}
			if(!(random || Misc.hasPermission(player, "luckyblock.command.get." + type.name()))) {
				player.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", LuckyBlockType.map().get(type).getCustomName()));
				return CommandResponce.SUCCESS;
			}
			
			int amount = 1;
			try {
				amount = Integer.parseInt(args[1]);
			} catch(Exception ignored) {}
			if(amount < 1) amount = 1;
			
			ItemStack stack = LuckyBlockType.map().get(type).getSkull();
			stack.setAmount(amount);
			player.getInventory().addItem(stack);
			player.sendMessage(Message.CMD_LB_RECEIVED.getAsString().replace("%lb%", type.getCustomName(true))
					.replace("%amount%", String.valueOf(amount)));
			return CommandResponce.SUCCESS;
			
		}
		return CommandResponce.SEND_HELP;
		
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_GET; }
	
}
