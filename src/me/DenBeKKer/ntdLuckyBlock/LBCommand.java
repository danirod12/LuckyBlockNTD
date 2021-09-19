package me.DenBeKKer.ntdLuckyBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.loader.ConvertManager;
import me.DenBeKKer.ntdLuckyBlock.util.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.GuiManager.GuiType;
import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;

public class LBCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length > 0 && (args[0].equalsIgnoreCase("version") ||
				args[0].equalsIgnoreCase("ver") || args[0].equalsIgnoreCase("v") ||
				args[0].equalsIgnoreCase("build"))) {
			
			sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fThis server is running \u00a7eLuckyBlock NTD\u00a7f plugin");
			sender.sendMessage("\u00a78 • \u00a7fRunning version - \u00a7e" + LBMain.getVersion() + " \u00a77(" +
					(LBMain.isPremium() ? "\u00a7bprem" : "\u00a7afree") + "\u00a77) " + (LBMain.needUpdate() ?
							"\u00a7c\u00a7l[!] \u00a7cVersion " + LBMain.getInstance().updater.getLatestVersion() + " available"
								: "\u00a7aLatest version"));
			sender.sendMessage("\u00a78 • \u00a7fBuild - \u00a7e" + LBMain.getBuild() + "\u00a7f, last update - \u00a7e" + LBMain.getLastUpdate());
			sender.sendMessage("\u00a78 • \u00a7fPlugin author - \u00a7eDenBeKKer \u00a77(Known as danirod12)");
			
//			Player danirod = Bukkit.getPlayerExact("Danirod_Gaming");
//			if(danirod != null && danirod.isOnline()) {
//				sender.sendMessage("\u00a78 • \u00a7cPlugin author is online - \u00a7oDanirod_Gaming");
//			}
			
			sender.sendMessage("\u00a78 • \u00a7fSpigotMC url - \u00a7bhttps://www.spigotmc.org/resources/92026/");
			sender.sendMessage("\u00a78 • \u00a7fbStats metrics - \u00a7bhttps://clck.ru/X9Z6e");
			
			return true;
		}
		
		author_info:
		if(sender instanceof Player) {
			
			if(LBMain.getInstance().dai_gui_get && args.length > 1 &&
					args[0].equalsIgnoreCase("gui") && args[1].equalsIgnoreCase("get")) {
				break author_info;
			}
			
			if(LBMain.isReduced()) {
				if(!LBMain.getReduced().contains((Player) sender)) {
					sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fRunning \u00a7antdLuckyBlock v"
							+ LBMain.getVersion() + " " + (LBMain.isPremium() ? "\u00a7dprem" : "free") + " \u00a7fby \u00a7aDenBeKKer");
					LBMain.getReduced().add((Player) sender);
				}
			} else sender.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7fRunning \u00a7antdLuckyBlock v"
					+ LBMain.getVersion() + " " + (LBMain.isPremium() ? "\u00a7dprem" : "free") + " \u00a7fby \u00a7aDenBeKKer");
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
			case "convert": {
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.convert") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				if(ConvertManager.getRequests() == 0) {
					sender.sendMessage("\u00a7cThere is no convert requests");
					return true;
				}
				
				if(!LBMain.isPremium()) {
					sender.sendMessage("\u00a7cThis feature available only in premium plugin version");
					return true;
				}
				
				// premium command realization
				return true;
				
			}
			case "iteminfo": {
				
				if(!(sender instanceof Player)) {
					sender.sendMessage("This subcommand only for players");
					return true;
				}
				
				if(sender instanceof Player && !((Player)sender).hasPermission("luckyblock.iteminfo") && !((Player)sender).hasPermission("luckyblock.*")) {
					sender.sendMessage(Message.CANT_USE_SUB.getAsString());
					return true;
				}
				
				Player player = (Player) sender;
				ItemStack item = LBMain.getInstance().factory.getItemInMainHand(player);
				
				if(item == null) {
					player.sendMessage("\u00a7cTake item in hand for this command");
					return true;
				}
				
				LuckyBlockType name = LuckyBlockAPI.getLuckyBlock(item, true, false);
				LuckyBlockType uuid = LuckyBlockAPI.getLuckyBlock(item, false, true);
				LuckyBlockType config = LuckyBlockAPI.getLuckyBlock(item, LBMain.isVerifyName(), LBMain.isVerifyUUID());
				
				if(name == null && uuid == null) {
					player.sendMessage("\u00a7cItem type - \u00a7e" + item.getType());
					return true;
				}
				
				if(name != null) {
					player.sendMessage("\u00a7cItem name \"\u00a7f" + item.getItemMeta().getDisplayName().replace("\u00a7", "&")
							+ "\u00a7c\" matches with \u00a7" + name.toColorSymbol() + name.name() + "\u00a7c luckyblock");
				}
				if(uuid != null) {
					player.sendMessage("\u00a7cItem UUID \"\u00a7f" + LBMain.getUUID(item)
							+ "\u00a7c\" matches with \u00a7" + uuid.toColorSymbol() + uuid.name() + "\u00a7c luckyblock");
				}
				
				if(config != null) {
					player.sendMessage("\u00a7cThis items matches with \u00a7" + config.toColorSymbol() + config.name() + "\u00a7c by config setup");
					if(!config.isLoaded()) {
						player.sendMessage("\u00a7cLuckyBlock for \u00a7" + config.toColorSymbol() + config.name() + " \u00a7cnot loaded");
					} else {
						LuckyBlock block;
						try {
							block = config.get();
						} catch (LuckyBlockNotLoadedException e) {
							e.printStackTrace();
							return true;
						}
						player.sendMessage("\u00a78 > \u00a7fEconomy enabled - \u00a7e" + block.canBeShoped()
							+ (block.canBeShoped() ? "\u00a77 (Price: " + block.getPrice() + ")" : ""));
						player.sendMessage("\u00a78 > \u00a7fCrafts enabled - \u00a7e" + block.getRecipes().size());
						player.sendMessage("\u00a78 > \u00a7fCustom name - \u00a7e" + block.getCustomName());
						player.sendMessage("\u00a78 > \u00a7fAvailable drops - \u00a7e" + block.items$mapped() + "/" + block.items$mappedTwice());
					}
				}
				player.sendMessage("\u00a7cItem type - \u00a7e" + item.getType());
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
				
				Collection<LuckyBlockType> disabled = new ArrayList<>();
				for(LuckyBlockType type : LuckyBlockType.values()) {
					
					if(!type.isLoaded()) {
						disabled.add(type);
						continue;
					}
					LuckyBlock block = null;
					try {
						block = type.get();
					} catch(LuckyBlockNotLoadedException e) {
						e.printStackTrace();
						continue;
					}
					
					sender.sendMessage("\u00a78 • \u00a7" + type.toColorSymbol() + type.name() + " \u00a7aenabled \u00a77(Loaded entries: " +
							block.items$mapped() + ", Enabled recipes: " + block.getRecipes().size() + ")");
					
				}
				disabled.forEach(n -> sender.sendMessage("\u00a78 • \u00a7" + n.toColorSymbol() + n.name() + " \u00a7cdisabled"));
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
