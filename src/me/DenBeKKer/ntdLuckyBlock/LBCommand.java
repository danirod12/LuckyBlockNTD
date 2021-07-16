package me.DenBeKKer.ntdLuckyBlock;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.util.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.GuiManager.GuiType;
import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager.Message;

public class LBCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			if(LBMain.isReduced()) {
				if(!LBMain.getReduced().contains((Player) sender)) {
					sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fRunning \u00a7antdLuckyBlock v"
							+ LBMain.getVersion() + " \u00a7fby \u00a7aDenBeKKer\u00a7f, " + (LBMain.isPremium() ? "\u00a7dpremium version" : "free version"));
					LBMain.getReduced().add((Player) sender);
				}
			}
		}
//		if(!(LBMain.isReduced() && sender instanceof Player && LBMain.getReduced().contains((Player) sender))) {
//			sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fRunning \u00a7antdLuckyBlock v" + LBMain.getVersion() + " \u00a7fby \u00a7aDenBeKKer");
//			if(LBMain.isReduced())
//				LBMain.getReduced().add((Player) sender);
//		}
		
		if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.use") && !((Player)sender).hasPermission("luckyblock.*")) {
			sender.sendMessage(Message.CANT_USE_ANY.getAsString());
			return true;
		}
		
		if(args.length > 0) {
			
			switch(args[0].toLowerCase()) {
			case "edit": {
				args = new String[] { "gui", "edit" };
				break;
			}
			default: break;
			}
			
			switch(args[0].toLowerCase()) {
			case "checkforupdates": case "update": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.checkforupdates") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				sender.sendMessage("\u00a7aChecking for updates...");
				LBMain.getInstance().checkForUpdates(true);
				if(!LBMain.needUpdate())
					sender.sendMessage("\u00a7aNo updates were found :(");
				return true;
			}
			case "support": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.support") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				LBMain.log(Level.INFO, ChatColor.WHITE + "=-= " + ChatColor.GOLD + "SUPPORT & BUG REPORTING & FEATURE REQUESTING " + ChatColor.WHITE + "=-=");
				LBMain.log(Level.INFO, ChatColor.DARK_GRAY + " > " + ChatColor.AQUA + "Discord " + ChatColor.WHITE + "- https://discord.gg/vbYW3sperj");
				LBMain.log(Level.INFO, ChatColor.DARK_GRAY + " > " + ChatColor.BLUE + "Vkontakte "
						+ ChatColor.WHITE + "- https://vk.com/danirodplay " + ChatColor.GRAY + "(Rus)");
				LBMain.log(Level.INFO, "\u00a78 > \u00a76API usage \u00a7f- https://clck.ru/V3DoJ");
				LBMain.log(Level.INFO, "\u00a78 > \u00a76LuckyBlock setup \u00a7f- https://clck.ru/VnqVN");
				LBMain.log(Level.INFO, ChatColor.WHITE + "=-= " + ChatColor.GOLD + "SUPPORT & BUG REPORTING & FEATURE REQUESTING " + ChatColor.WHITE + "=-=");
				sender.sendMessage("\u00a7f=-= \u00a76SUPPORT & BUG REPORTING & FEATURE REQUESTING \u00a7f=-=");
				sender.sendMessage("\u00a78 > \u00a7bDiscord \u00a7f- https://discord.gg/vbYW3sperj");
				sender.sendMessage("\u00a78 > \u00a79Vkontakte \u00a7f- https://vk.com/danirodplay \u00a77(Rus)");
				sender.sendMessage("\u00a78 > \u00a76API usage \u00a7f- https://clck.ru/V3DoJ");
				sender.sendMessage("\u00a78 > \u00a76LuckyBlock setup \u00a7f- https://clck.ru/VnqVN");
				sender.sendMessage("\u00a7f=-= \u00a76SUPPORT & BUG REPORTING & FEATURE REQUESTING \u00a7f=-=");
				return true;
			}
			case "get": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.get") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage("This subcommand only for players");
					return true;
				}
				
				if(args.length > 1) {
					
					try {
						LuckyBlockType type = LuckyBlockType.valueOf(args[1].toUpperCase());
						if(type == null) {
							sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[1]));
							return true;
						}
						if(type.get() == null) {
							sender.sendMessage(Message.CMD_LB_DISABLED.getAsString().replace("%lb%", args[1]));
							return true;
						}
						
						if(sender instanceof Player && LBMain.getInstance().config.get().getBoolean("permission-for-each-give-get") &&
								!((Player)sender).hasPermission("luckyblock.get." + type.name().toLowerCase())
								&& !((Player)sender).hasPermission("luckyblock.get.*")) {
							sender.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", type.get().getCustomName()));
							return true;
						}
						
						int amount = 1;
						try {
							amount = Integer.parseInt(args[2]);
						} catch(Exception ignored) {}
						if(amount < 1) amount = 1;
						
						ItemStack item = type.get().getSkull();
						item.setAmount(amount);
						((Player)sender).getInventory().addItem(item);
						item.setAmount(1);
						sender.sendMessage(Message.CMD_LB_RECEIVED.getAsString().replace("%lb%", type.getCustomName()).replace("%amount%", String.valueOf(amount)));
						return true;
					} catch(Exception ex) {
						if(ex.getMessage().contains("enum"))
							sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[1]));
						else ex.printStackTrace();
						return true;
					}
					
				}
				break;
				
			}
			case "reload": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.reload") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				long ms = System.currentTimeMillis();
				
				LBMain.getInstance().loadConfig();
//				MessagesManager.reload(LBMain.getInstance().config.get().getString("language"));
				LBMain.getInstance().system_load();
				try {
					GuiManager.init();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				sender.sendMessage(Message.RELOADED_CONFIG.getAsString().replace("%amount%", String.valueOf(LuckyBlockType.map().size())));
				LBMain.log(Level.INFO, "Reloaded (took " + (System.currentTimeMillis() - ms) + " ms)... ");
				return true;
				
			}
			case "list": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.list") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				for(LuckyBlockType type : LuckyBlockType.values()) {
					sender.sendMessage("\u00a78 • \u00a7e" + type.name() + ((type.get() == null) ? " \u00a7cdisabled or not loaded" : (
							" \u00a7aenabled \u00a77(" + type.get().getCustomName() + "\u00a77)")));
				}
				return true;
				
			}
//			case "edit": {
//				
//				if(!(sender instanceof Player)) {
//					sender.sendMessage("This subcommand only for players");
//					return true;
//				}
//				
//				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.gui.edit") && !((Player)sender).hasPermission("luckyblock.*")) {
//					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
//					return true;
//				}
//				
//				GuiManager.open(GuiType.EDIT, (Player)sender);
//				return true;
//			}
			case "gui": {
				
				if(!(sender instanceof Player)) {
					sender.sendMessage("This subcommand only for players");
					return true;
				}
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.gui") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				Player player = (Player) sender;
				if(args.length > 1) {
					
					switch(args[1].toLowerCase()) {
					case "get":
						
						if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.gui.get") && !((Player)sender).hasPermission("luckyblock.*")
								 && !((Player)sender).hasPermission("luckyblock.gui.*")) {
							sender.sendMessage(Message.CANT_USE_SUB.getAsString());
							return true;
						}
						
						GuiManager.open(GuiType.GET, player);
						
						return true;
						
					case "edit":
						
						if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.gui.edit") && !((Player)sender).hasPermission("luckyblock.*")
								 && !((Player)sender).hasPermission("luckyblock.gui.*")) {
							sender.sendMessage(Message.CANT_USE_SUB.getAsString());
							return true;
						}

						GuiManager.open(GuiType.EDIT, player);
						return true;
					default: break;
					}
					
				}
				
			}
			case "give": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.give") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				if(args.length > 2) {
					
					Player p = Bukkit.getPlayerExact(args[1]);
					if(p == null) {
						sender.sendMessage(Message.CMD_PLAYER_NOT_FOUND.getAsString().replace("%player%", args[1]));
						return true;
					}
					
					try {
						LuckyBlockType type = LuckyBlockType.valueOf(args[2].toUpperCase());
						if(type == null) {
							sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[2]));
							return true;
						}
						if(type.get() == null) {
							sender.sendMessage(Message.CMD_LB_DISABLED.getAsString().replace("%lb%", args[2]));
							return true;
						}
						
						if(sender instanceof Player && LBMain.getInstance().config.get().getBoolean("permission-for-each-give-get") &&
								!((Player)sender).hasPermission("luckyblock.get." + type.name().toLowerCase())
								&& !((Player)sender).hasPermission("luckyblock.get.*")) {
							sender.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString().replace("%lb%", type.get().getCustomName()));
							return true;
						}
						
						int amount = 1;
						try {
							amount = Integer.parseInt(args[3]);
						} catch(Exception ignored) {}
						if(amount < 1) amount = 1;
						
						ItemStack item = type.get().getSkull();
						item.setAmount(amount);
						p.getInventory().addItem(item);
						item.setAmount(1);
						sender.sendMessage(Message.CMD_LB_GIVE.getAsString().replace("%lb%", type.getCustomName()).replace("%amount%", String.valueOf(amount)));
						return true;
					} catch(Exception ex) {
						if(ex.getMessage().contains("enum"))
							sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[2]));
						else ex.printStackTrace();
						return true;
					}
					
				}
				break;
				
			}
			}
		}
		
		for(String y : Message.CMD_HELP.getAsList(true)) {
			sender.sendMessage(y.replace("%label%", label));
		}
//		sender.sendMessage("\u00a7eLuckyBlock's \u00a7fcommands help:");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " get <color> [amount] \u00a7f- get luckyblock");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " give <player> <color> [amount] \u00a7f- give luckyblock");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " gui <get/edit> \u00a7f- open gui");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " edit \u00a7f- aliase of edit gui");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " list \u00a7f- list of luckyblock types");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " reload \u00a7f- reload all configs");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " checkforupdates \u00a7f- check for updates");
//		sender.sendMessage("\u00a78 > \u00a7e/" + label + " support \u00a7f- get author contacts");
		return true;
	}
	
}
