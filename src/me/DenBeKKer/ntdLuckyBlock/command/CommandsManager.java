package me.DenBeKKer.ntdLuckyBlock.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.ConvertCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.DestroyCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.GetCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.GetCustomItemCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.GiveCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.GuiCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.ItemInfoCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.ListCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.ListCustomItemsCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.ReloadCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.SupportCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.UpdatesCommand;
import me.DenBeKKer.ntdLuckyBlock.command.cmd.VersionCommand;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class CommandsManager implements CommandExecutor, TabCompleter {
	
	private final Collection<Player> reduced = new ArrayList<>();
	private final Collection<LBCommand> commands = new ArrayList<>();
	
	public void fix_ram(Player player) {
		if(player == null) {
			reduced.clear();
			return;
		}
		reduced.remove(player);
	}
	
	public void register(LBCommand command) { commands.add(command); }
	
	public CommandsManager() {
		
		// Basic
		register(new GetCommand());
		register(new GiveCommand());
		register(new GuiCommand());
		
		// System
		register(new SupportCommand());
		register(new UpdatesCommand());
		register(new ReloadCommand());
		register(new DestroyCommand());
		
		// Items Management
		register(new ListCommand());
		register(new ListCustomItemsCommand());
		register(new GetCustomItemCommand());
		register(new ItemInfoCommand());
		
		// Other
		register(new VersionCommand());
		register(new ConvertCommand());
		
		LBMain.debug("Loaded " + commands.size() + " subcommands");
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		n:
		if(sender instanceof Player && !LBMain.getInstance().disable_author_info) {
			
			if(LBMain.isReduced() && reduced.contains((Player) sender)) break n;
			
			sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fRunning \u00a7antdLuckyBlock v"
					+ LBMain.getVersion() + " " + (LBMain.isPremium() ? "\u00a7dprem" : "free") + " \u00a7fby \u00a7aDenBeKKer");
			if(LBMain.isReduced()) reduced.add((Player) sender);
			
		}
		
		n:
		if(args.length > 0) {
			
			LBCommand command = fetchCommand(args[0]);
			if(command == null) break n;
			
			if(sender instanceof Player) {
				
				if(command.permission() && !Misc.hasPermission((Player) sender, "luckyblock.command." + args[0].toLowerCase())) {
					sender.sendMessage(Message.CMD_NO_PERM.getAsString(true));
					return true;
				}
				
			} else {
				
				if(command.onlyPlayer()) {
					sender.sendMessage("Sorry, but command `/" + label + " " + args[0] + "` only for players");
					return true;
				}
				
			}
			
			try {
				
				CommandResponce responce = command.execute(sender, label, args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
				if(responce == CommandResponce.SUCCESS) return true;
				if(responce == CommandResponce.MISSED_PERMISSION) {
					sender.sendMessage(Message.CMD_NO_PERM.getAsString(true));
					return true;
				}
				
			} catch(Throwable th) {
				th.printStackTrace();
				sender.sendMessage("\u00a7cAn exception ocurred while performing command /" + label + " " + args[0]);
				sender.sendMessage("\u00a78 - \u00a7fIf you are a player - \u00a7aReport this to staff");
				sender.sendMessage("\u00a78 - \u00a7fIf you are a staff - Check console & report to author");
				return true;
			}
			
		}
		
		final List<String> messages = commands.stream().filter(command -> {
			if(sender instanceof Player)
				return !(command.permission() && !Misc.hasPermission((Player) sender, "luckyblock.command." + command.commands()[0]));
			else return !command.onlyPlayer();
		}).filter(n -> n.helpMessage() != null).map(n -> n.helpMessage().getAsString(true).replace("%label%", label)).collect(Collectors.toList());
		if(messages.size() == 0)
			return true;
		else {
			sender.sendMessage(Message.CMD_HELP.getAsString(true));
			messages.forEach(n -> sender.sendMessage(n));
		}
		return true;
		
	}
	
	private LBCommand fetchCommand(String string) {
		
		for(LBCommand command : commands)
			for(String cmd : command.commands())
				if(cmd.equalsIgnoreCase(string)) return command;
		return null;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length > 1) {
			
			if(sender instanceof Player && args[0].equalsIgnoreCase("get") && (args.length < 3)) {
				
				return LuckyBlockType.enabled().stream().map(n -> n.name())
						.filter(n -> Misc.hasPermission((Player) sender, "luckyblock.command.get." + n))
						.filter(n -> n.startsWith(args[1].toUpperCase())).collect(Collectors.toList());
				
			}
			if(args[0].equalsIgnoreCase("give") && (!(sender instanceof Player) || Misc.hasPermission((Player) sender, "luckyblock.command.give"))) {
				
				if(args.length == 2) return Bukkit.getOnlinePlayers().stream().map(n -> n.getName())
						.filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
				if(args.length == 3) return LuckyBlockType.enabled().stream().map(n -> n.name())
						.filter(n -> Misc.hasPermission((Player) sender, "luckyblock.command.give." + n))
						.filter(n -> n.startsWith(args[2].toUpperCase())).collect(Collectors.toList());
				
			}
			if((args[0].equalsIgnoreCase("customitemget") ||
					args[0].equalsIgnoreCase("getcustomitem") ||
					args[0].equalsIgnoreCase("cig") || args[0].equalsIgnoreCase("gci"))
					 && (!(sender instanceof Player) || Misc.hasPermission((Player) sender, "luckyblock.command.customitemget"))) {
				
				if(args.length == 2) return CustomItemFactory.copy().stream().map(n -> n.getIdentifier().toString())
						.filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()) ||
								!args[1].contains("-") && n.split("-")[1].startsWith(args[1].toLowerCase())).collect(Collectors.toList());
				
			}
			return new ArrayList<>();
		}
		
		return commands.stream().filter(command -> {
			
			if(sender instanceof Player)
				return !(command.permission() && !Misc.hasPermission((Player) sender, "luckyblock.command." + command.commands()[0]));
			else return !command.onlyPlayer();
			
		}).map(n -> n.commands()[0]).filter(n -> n.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		
	}
	
}
