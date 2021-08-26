package me.DenBeKKer.ntdLuckyBlock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.DenBeKKer.ntdLuckyBlock.factory.LBFactory;
import me.DenBeKKer.ntdLuckyBlock.sk89q.LBWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.sk89q.LBWorldGuard;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager;
import me.DenBeKKer.ntdLuckyBlock.util.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.Metrics;
import me.DenBeKKer.ntdLuckyBlock.util.SpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import net.milkbowl.vault.economy.Economy;

public class LBMain extends JavaPlugin {
	
	private static LBMain instance;
	private static boolean need_update = false;
	public Config config;
	public IMat factory;
	private static boolean inform;
	private static File folder, schematics_folder;
	private static String version;
	public SpigotUpdater updater;
	public Economy eco;
	private static Collection<Player> reduce$list = new ArrayList<>();
	private static boolean reduce = false;
	private static boolean brperm = true;
	private static boolean debug;
	private static boolean schematics = false;
	
	
	// Last update date & Build number
	private static final String last_update = "26/08/2021";
	private static final int build = 51;
	// Last update date & Build number
	
	
	private static boolean premium = false;
	public static boolean isPremium() { return premium; }
	
	public static LBMain getInstance() { return instance; }
	public static File getFolder() { return folder; }
	public static String getVersion() { return version; }
	public static String getLastUpdate() { return last_update; }
	public static int getBuild() { return build; }
	public static File getSchematicsFolder() { return schematics_folder; }
	public static boolean getIsSk89q() { return schematics; }
	public static boolean isReduced() { return reduce; }
	public static boolean isBreakPermissions() { return brperm; }
	public static Collection<Player> getReduced() { return reduce$list; }
	
	public static boolean isDebug() { return debug; }
	@Deprecated
	public static boolean getDebug() { return debug; }
	
	public static Class<?> getClass(String path) {
		try {
			return Class.forName(path);
		} catch(Throwable th) { return null; }
	}
	
	public static void log(Level level, String message) {
		
		if(level == Level.INFO)
			Bukkit.getConsoleSender().sendMessage("[ntdLuckyBlock] " + message.replace("&", "\u00a7"));
		else LBMain.getInstance().getLogger().log(level, message.replace("&", "\u00a7"));
		
	}
	
	public void onDisable() {
		if(GuiManager.isInited())
			GuiManager.close();
	}
	
	public void onLoad() { LBWorldGuard.register(); }
	
	public void onEnable() {
		
		long ms = System.currentTimeMillis();
		
		instance = this;
		updater = new SpigotUpdater(instance, 92026);
		folder = new File(instance.getDataFolder() + File.separator + "luckyblocks");
		folder.mkdirs();
		version = getDescription().getVersion();
		
		Metrics metrics = new Metrics(this, 11218);
		metrics.addCustomChart(new Metrics.SimplePie("enabled_luckyblocks", () -> {
			return String.valueOf(LuckyBlockType.map().size());
		}));
		metrics.addCustomChart(new Metrics.SimplePie("luckydrop_types", () -> {
			int a = 0, b = 0;
			for(LuckyBlock z : LuckyBlockType.map().values()) {
				a += z.items$mapped();
				b += z.items$mappedTwice();
			}
			return a + "/" + b;
			
		}));
		metrics.addCustomChart(new Metrics.SimplePie("version_type", () -> {
			return premium ? "premium" : "free";
		}));
		
		log(Level.INFO, ChatColor.WHITE + "Starting LuckyBlock (ntdLuckyBlock) v" + version + ", build" + getBuild()
			+ ", " + (premium ? "premium" : "free") + " version");
		log(Level.INFO, ChatColor.YELLOW + "  ╭╮╱╱╭╮╱╭┳━━━┳╮╭━┳╮╱╱╭┳━━╮╭╮╱╱╭━━━┳━━━┳╮╭━╮");
		log(Level.INFO, ChatColor.YELLOW + "  ┃┃╱╱┃┃╱┃┃╭━╮┃┃┃╭┫╰╮╭╯┃╭╮┃┃┃╱╱┃╭━╮┃╭━╮┃┃┃╭╯");
		log(Level.INFO, ChatColor.YELLOW + "  ┃┃╱╱┃┃╱┃┃┃╱╰┫╰╯╯╰╮╰╯╭┫╰╯╰┫┃╱╱┃┃╱┃┃┃╱╰┫╰╯╯");
		log(Level.INFO, ChatColor.YELLOW + "  ┃┃╱╭┫┃╱┃┃┃╱╭┫╭╮┃╱╰╮╭╯┃╭━╮┃┃╱╭┫┃╱┃┃┃╱╭┫╭╮┃");
		log(Level.INFO, ChatColor.YELLOW + "  ┃╰━╯┃╰━╯┃╰━╯┃┃┃╰╮╱┃┃╱┃╰━╯┃╰━╯┃╰━╯┃╰━╯┃┃┃╰╮");
		log(Level.INFO, ChatColor.YELLOW + "  ╰━━━┻━━━┻━━━┻╯╰━╯╱╰╯╱╰━━━┻━━━┻━━━┻━━━┻╯╰━╯ NTD");
		log(Level.INFO, ChatColor.WHITE + "          Made with " + ChatColor.RED + "love " + ChatColor.WHITE + "by " + ChatColor.GREEN + "DenBeKKer");
		log(Level.INFO, ChatColor.WHITE + "");
		log(Level.INFO, ChatColor.WHITE + "=-= " + ChatColor.GOLD + "SUPPORT & BUG REPORTING & FEATURE REQUESTING " + ChatColor.WHITE + "=-=");
		log(Level.INFO, ChatColor.DARK_GRAY + " > " + ChatColor.AQUA + "Discord " + ChatColor.WHITE + "- https://discord.gg/vbYW3sperj");
		log(Level.INFO, ChatColor.DARK_GRAY + " > " + ChatColor.BLUE + "Vkontakte "
				+ ChatColor.WHITE + "- https://vk.com/danirodplay " + ChatColor.GRAY + "(Rus)");
		log(Level.INFO, ChatColor.WHITE + "=-= " + ChatColor.GOLD + "SUPPORT & BUG REPORTING & FEATURE REQUESTING " + ChatColor.WHITE + "=-=");
		log(Level.INFO, ChatColor.RED + "Help me make my plugin better! Fill out " + ChatColor.GOLD + "https://forms.gle/GvX5pB4BXU7BhAEi9");
		
		loadConfig();
		
//		checkForUpdates();
		if(config.get().getBoolean("scheduled-update-check")) {
			if(debug) debug("Loading Bukkit scheduler thread for delayed update check");
			Bukkit.getScheduler().runTaskTimer(instance, () -> checkForUpdates(), 100, 100000);
		} else Bukkit.getScheduler().runTaskLater(instance, () -> checkForUpdates(), 100);
		
		boolean old = false;
		try {
			Material mat = Material.valueOf("PLAYER_HEAD");
			if(mat == null) old = true;
		} catch(Exception ex) {
			old = true;
		}
		
		factory = old ? new Mat1_12() : new Mat1_13();
		log(Level.INFO, Message.MATERIAL_API.getAsString().replace("%build%", factory.build()));
		
		for(PlayerHead h : PlayerHead.values()) h.loadHead();
		Hooks.loadAll();
		
		if(Hooks.Vault.isEnabled()) {
			if(debug) debug("Loading Vault provider");
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
	        if (economyProvider != null && economyProvider.getProvider() != null) {
	            eco = economyProvider.getProvider();
	        } else Hooks.Vault.disable("economy not found");
		}
		
		schematics_folder = new File(getDataFolder(), "schematics");
		if(Hooks.WorldEdit.isEnabled()) {
			if(debug) debug("Loading WorldEdit provider");
			
			if(!LBWorldEdit.isPlatformAvailable()) {
				
				Hooks.WorldEdit.disable("unsupported version");
				
			} else {
				
				if(!schematics_folder.exists()) {
					schematics_folder.mkdirs();
					new Config(instance, "configuration.schematics." + factory.build(), schematics_folder, "cage_lava.schem" +
							(factory.build().equalsIgnoreCase("old") ? "atic" : "")).copy(false);
					new Config(instance, "configuration.schematics.main", schematics_folder, "bedrock_problem.schematic").copy(false);
					if(!old) new Config(instance, "configuration.schematics.main", schematics_folder, "small_temple.schematic").copy(false);
				}
				
				schematics = true;
				
				if(debug)
					debug("Hooked into " + (LBWorldEdit.isFAWE() ? "FastAsync" : "") + "WorldEdit");
				
			}
			
			if(Hooks.WorldGuard.isEnabled() && !LBWorldGuard.isAvailable())
				Hooks.WorldGuard.disable("unsupported version");
			
		}
		
		Hooks.print();
		system_load();
		
		try {
			GuiManager.init();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		if(debug) debug("Loading Events");
		Bukkit.getPluginManager().registerEvents(new LBHandler(), this);
		Bukkit.getPluginManager().registerEvents(new GuiManager(), this);
		
		Bukkit.getScheduler().runTaskLater(instance, () -> {
			
			log(Level.INFO, "If you are love my plugin, support me with 5 stars review (https://www.spigotmc.org/resources/92026/) or with " +
					"https://paypal.me/denbekker");
			Bukkit.getOnlinePlayers().stream().filter(n -> n.hasPermission("luckyblock.supportme")).forEach(n -> {
				n.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7eIf you are love my plugin, support me with \u00a765 stars review"
						+ " \u00a7e(\u00a7b https://www.spigotmc.org/resources/92026/ \u00a7e) or with \u00a79https://paypal.me/denbekker");
			});
			
		}, 120);
		
		if(debug) debug("Loading Command");
		Bukkit.getPluginCommand("ntdluckyblock").setExecutor(new LBCommand());
		ms = (System.currentTimeMillis() - ms);
		log(Level.INFO, ChatColor.GREEN + "Enabled " + msIndicator(ms, 500, 1000) + "(took " + ms + " ms)" + ChatColor.GREEN + "... ");
//		if(ms > 1000)
//			log(Level.INFO, "\u00a7c\u00a7l[!] \u00a7ePlugin initialization took over 1 second (1000 ms). This is fine for the first launch. "
//					+ "But if this is not the first launch, then inform the author about this by providing the full console log");
		if(ms > 1000)
			log(Level.INFO, "\u00a7c\u00a7l[!] \u00a7ePlugin initialization took over 1 second (" + ms + " ms)");
		
	}
	
	private ChatColor msIndicator(long ms, int yellow, int red) {
		if(ms > red) return ChatColor.RED;
		if(ms > yellow) return ChatColor.YELLOW;
		return ChatColor.GREEN;
	}
	
	public void loadConfig() {
		
		//log(Level.INFO, Message.LOADING_CONFIG.get());
		
		config = new Config(instance, getDataFolder(), "config.yml");
		config.copy(true);
		
		if(config.get().getString("config-level") == null ||
				config.get().getString("config-level").equalsIgnoreCase("1.2") ||
				config.get().getString("config-level").equalsIgnoreCase("1.1") ||
				config.get().getString("config-level").equalsIgnoreCase("1.0")) {
			
			getLogger().log(Level.WARNING, "Your config have old version and will be updated");	
			getLogger().log(Level.WARNING, "All settings into \"config.yml\" will be deleted");
			getLogger().log(Level.WARNING, "SETUP CONFIG AND PERFORM /ntdluckyblock reload");
			
			getLogger().log(Level.INFO, Message.LOADING_CONFIG.getAsString());
			config.delete();
			config.copy(true);
			
		} else {
			if(config.get().getString("config-level").equalsIgnoreCase("1.3")) {
				getLogger().log(Level.INFO, "Your config level is 1.3, updating to 1.4...");
				config.get().set("scheduled-update-check", true);
				config.get().set("debug", false);
				config.get().set("config-level", "1.4");
				config.save();
			}
			if(config.get().getString("config-level").equalsIgnoreCase("1.4")) {
				getLogger().log(Level.INFO, "Your config level is 1.4, updating to 1.5...");
				config.get().set("reduce-command-author-info", false);
				
				File messages = new File(getDataFolder() + File.separator + "messages.yml");
				
				boolean custom = true;
				if(messages.exists()) {
					
					getLogger().log(Level.WARNING, "Moving messages.yml to /lang/custom.yml");	
					try {
						File file = new File(MessagesManager.lang_folder + File.separator + "custom.yml");
						file.createNewFile();
						Files.move(messages, file);
					} catch (IOException e) {
						e.printStackTrace();
						custom = false;
					}
					if(custom)
						messages.delete();
					
				}
				
				config.get().set("language", custom ? "custom" : "en");
				config.get().set("config-level", "1.5");
				config.save();
			}
			if(config.get().getString("config-level").equalsIgnoreCase("1.5")) {
				getLogger().log(Level.INFO, "Your config level is 1.5, updating to 1.6...");
				config.get().set("break-permissions", true);
				config.get().set("config-level", "1.6");
				config.save();
			}
		}
		
		brperm = config.get().getBoolean("break-permissions");
		reduce = config.get().getBoolean("reduce-command-author-info");
		MessagesManager.reload(config.get().getString("language"));
		
		debug = config.get().getBoolean("debug");
		if(debug) getLogger().log(Level.WARNING, "Debug mode enabled! Note that this mode only for developer!");
		inform = config.get().getBoolean("inform-about-update");
		
		piston_fix = config.get().getBoolean("beta.pistonfix", true);
		explosion_fix = config.get().getBoolean("beta.explosionfix", true);
		
	}
	
	public static void debug(String string) { log(Level.INFO, "[DEBUG] " + string); }
	
	public void system_load() {
		
		long ms = System.currentTimeMillis();
		if(debug) debug("Loading system");
		map.clear();
		
		reduce$list = new ArrayList<>();
		
		for(String str : config.get().getStringList("enabled")) { try {
			LuckyBlockType lb = LuckyBlockType.valueOf(str.toUpperCase());
			if(lb == null) {
				log(Level.SEVERE, Message.LB_NOT_FOUND.getAsString().replace("%lb%", str.toUpperCase()));
				continue;
			}
			String e = lb.load();
			if(e != null) {
				log(Level.SEVERE, Message.LB_LOADING_EXCEPTION.getAsString().replace("%lb%", str.toUpperCase()).replace("%exception%", e));
				continue;
			}
		} catch(Exception ex) {
			log(Level.SEVERE, Message.LB_UNKNOWN_EXCEPTION.getAsString().replace("%lb%", str.toUpperCase()));
			ex.printStackTrace();
			continue;
		} }
		
		if(map.size() == 0) {
			log(Level.WARNING, Message.LB_LOADED_ZERO.getAsString());
		} else {
			log(Level.INFO, Message.LB_LOADED_NOT_ZERO.getAsString().replace("%amount%", String.valueOf(map.size())));
		}
		log(Level.INFO, "System loaded (took " + (System.currentTimeMillis() - ms) + " ms)...");
		
	}
	
	public void checkForUpdates() { checkForUpdates(false); }
	
	public void checkForUpdates(boolean b) {
		
		try {
			need_update = updater.checkForUpdates();
		} catch (Exception e) {
			instance.getLogger().log(Level.WARNING, "SpigotMC servers is unavailable... Check plugin page for updates " + updateLink());
		}
		
		if(need_update) {
			
			log(Level.INFO, ChatColor.GOLD + "╔");
			log(Level.INFO, ChatColor.GOLD + "║   " + ChatColor.RED + ChatColor.BOLD + "[!] " + ChatColor.GREEN +
					"New plugin version for " + ChatColor.YELLOW + "LuckyBlock" + ChatColor.GREEN + " has been released!");
			log(Level.INFO,  ChatColor.GOLD + "║ " + ChatColor.GREEN + "Your current version is " + ChatColor.GRAY + 
					version + " (outdated)" + ChatColor.GREEN + ". New version is " + ChatColor.RED +  updater.getLatestVersion());
			log(Level.INFO, ChatColor.GOLD + "║ " + ChatColor.GREEN + "Check " + ChatColor.AQUA + LBMain.updateLink() +
					ChatColor.GREEN + " for more details");
			log(Level.INFO, ChatColor.GOLD + "╚");
			
			if(inform || b) for(Player p : Bukkit.getOnlinePlayers()) {
			
			if(p.hasPermission("luckyblock.update")) informAboutUpdate(p);
			
		} }
		
	}
	
	public void informAboutUpdate(Player p) {
		p.sendMessage("\u00a76╔");
		p.sendMessage("\u00a76║   \u00a7c\u00a7l[!] \u00a7aNew plugin version for \u00a7eLuckyBlock\u00a7a has been released!");
		p.sendMessage("\u00a76║ \u00a7aYour current version is \u00a77"
				+ LBMain.getVersion() + "\u00a7a. New version is \u00a7c" + LBMain.getInstance().updater.getLatestVersion());
		p.sendMessage("\u00a76║ \u00a7aCheck \u00a7b" + LBMain.updateLink() + "  \u00a76\u00a7l^_^");
		p.sendMessage("\u00a76╚");
	}
	
	private static HashMap<LuckyBlockType, LuckyBlock> map = new HashMap<>();
	
	public enum Hooks {
		
		Vault,
		WorldEdit,
		WorldGuard;
		
		private String error = "not found";
		
		public static void loadAll() {
			
			Arrays.asList(Hooks.values()).forEach(n -> n.load());
			
		}
		
		public static void print() {
			
			Collection<Hooks> collection = new ArrayList<>();
			for(Hooks hook : Hooks.values())
				if(hook.isEnabled())
					log(Level.INFO, ChatColor.GREEN + hook.name() + " connected");
				else collection.add(hook);
			
			for(Hooks hook : collection)
				log(Level.INFO, ChatColor.RED + hook.name() + " " + hook.error);
			
			log(Level.INFO,
					ChatColor.GOLD + "Found " + (Hooks.values().length - collection.size()) + "/" + Hooks.values().length + " compatible plugins");
			
		}
		
		public void disable(String reason) { this.error = reason; }
		
		public boolean isEnabled() { return error == null; }
		
		public void load() {
			
			if(Bukkit.getPluginManager().isPluginEnabled(this.name())) error = null;
			
		}
		
	}
	
	public enum LuckyBlockType {
		
		BLACK("c7b187e38b407feabaf190879d98a67f4c7052f3201f72e44571f537ea89d4c7"),
		BLUE("8534b17d2d3b5a64c57f5f080dd777945761d9f71d82e8f599f242976e4d0c05"),
		BROWN("c133414759f9e5315156017023a8b1b31cfbf177dcafb0603b8e9d94036a23f1"),
		CYAN("300c817432585fadcbd9ca8cfedfeedbce5851b7c07d1b073fb52fa8283e8f23"),
		GRAY("f171a0c0572824da80366d71c5413a7e3355abec79b8f529f1dc9dad2632f485"),
		GREEN("6ab180e820f71629f01f25b62bdeb9c324d50a38af74e6a9321439471046dc41"),
		LIGHT_BLUE("65fed46b39c6c34d491143b72ce728797d16f9b5aa40bbdec61667f5ff9d3d45"),
		LIGHT_GRAY("d019fe633a45fc3d353f9a2f09fd88e721de31c2bc969ae0aeeadf57c1be0bcc"),
		LIME("b03f5a9bc719cd1127be0954c73cd5714ecd478dad880ef2b9979c481097e6be"),
		MAGENTA("1f23685c697ba55edda425ecf1fec72feeb7bb985c6506b2cef070515e4e5492"),
		ORANGE("bd3a1436e0fd5f3e4d113a092c1d07a12e22d426c50183ccd62877d9cd885fbd"),
		PINK("11a367164bc6852c8f078811de7baacaefd6964960ebff1fa504c7b4b1298a52"),
		PURPLE("6cfb4ebc3ba4fec04d8978a1660bb7c98e96342ff2a238ada9df2f74bf032b40"),
		RED("29942dd1338abeae8b8274a41ae1dcdf2b7be449f28d6b650ec06e491e70f570"),
		WHITE("c8e16e7ff13d3e30a3a2b835a86fd05ecbc08909f6056de1646fe25b3def9584"),
		YELLOW("17bc1b64cba3dc4cefe4e121c3cdbbb0fa99aba0e113b5c916815fc9b304e636");
		
		private String texture;
		
		LuckyBlockType(String texture) {
			this.texture = texture;
		}
		
		public DyeColor asDye() {
			
			if(this == LuckyBlockType.LIGHT_GRAY && instance.factory instanceof Mat1_12) {
				return DyeColor.valueOf("SILVER");
			}
			
			return DyeColor.valueOf(this.name());
			
		}
		
		public static LuckyBlockType parse(DyeColor dye) { return LuckyBlockType.valueOf(dye.name()); }
		
		public static LuckyBlockType parse(String color) {
			try {
				return LuckyBlockType.valueOf(color.toUpperCase());
			} catch(Exception ex) {
				return null;
			}
		}
		
		public String load() {
			
			Config config = new Config(instance, "configuration." + instance.factory.build(), folder, this.name().toLowerCase() + ".yml");
			
			if(!config.hasResource()) {
				
				config.write();
				config.reload();
				FileConfiguration file = config.get();
				
				LBFactory.latest().generate(file, this);
				
				config.save();
//				instance.getLogger().log(Level.INFO, "Build-in config not found for " + this.name() + ". Setup " + config.getName() + " ^_^");
				
			}
			
			config.copy(true);
			LuckyBlock block = new LuckyBlock(this, config);
			if(block.getException() != null) return block.getException();
			map.put(this, block);
			return null;
			
		}
		
		@Deprecated
		public Material getMaterial() { return instance.factory.getGlass(asDye(), 1).getType(); }
		
		public ItemStack getItem() {
			return instance.factory.getGlass(asDye(), 1);
		}
		
		public LuckyBlock get() { return map.get(this); }
		
		public static Set<LuckyBlockType> list() { return map.keySet(); }
		
		public static HashMap<LuckyBlockType, LuckyBlock> map() { return map; }
		
		@SuppressWarnings("deprecation")
		public String fixName(String name) {
			
			try {
				while(name.length() >= 2 && (name.charAt(name.length() - 2) == '&' || name.charAt(name.length() - 2) == '\u00a7')) {
					name = name.substring(0, name.length() - 2);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			String name0 = name + "\u00a7" + toColor(this.asDye().getWoolData());
			if(debug) debug(("Remmaped " + name + " to " + name0 + " to avoid same names").replace("\u00a7", "&"));
			return name0;
			
		}
		
		public String getCustomName() {
			return map.containsKey(this) ? map.get(this).getCustomName() : this.name();
		}
		
		public String getTexture() { return this.texture; }
		
		@SuppressWarnings("deprecation")
		public UUID getUUID() {
			return UUID.fromString("12345678-1234-1234-1234-00000000000" + Integer.toHexString(asDye().getWoolData()));
		}
		
		@SuppressWarnings("deprecation")
		public String toColorSymbol() { return toColor(this.asDye().getWoolData()); }
		
		public static LuckyBlockType random(boolean b) {
			
			LuckyBlockType[] types = b ?
					LuckyBlockType.map().keySet().toArray(new LuckyBlockType[LuckyBlockType.map().size()]) : LuckyBlockType.values();
			
			if(types.length == 0)
				types = LuckyBlockType.values();
			
			return types[ThreadLocalRandom.current().nextInt(types.length)];
			
		}
		
	}
	
	public static String toColor(byte data) {
		
		switch(data) {
		
		case 0: return "f";
		case 1: return "6";
		case 2: return "d";
		case 3: return "b";
		case 4: return "e";
		case 5: return "a";
		case 6: return "d";
		case 7: return "8";
		case 8: return "7";
		case 9: return "3";
		case 10: return "5";
		case 11: return "1";
		//case 12: return "";
		case 13: return "2";
		case 14: return "4";
		case 15: return "0";
		
		}
		return "c";
		
	}
	
	private static HashMap<PlayerHead, ItemStack> heads = new HashMap<>();
	
	public static ItemStack getHead0(String url, String name, List<String> lore) {
		ItemStack head = instance.factory.getItem(Mat.PLAYER_SKULL, 1);
        if(url.isEmpty()) return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        headMeta.setDisplayName(name);
        headMeta.setLore(lore);
        head.setItemMeta(headMeta);
        return head;
	}
	
	public enum PlayerHead {
		
		PLUS_WOOD("http://textures.minecraft.net/texture/3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"),
		PLUS_STONE("http://textures.minecraft.net/texture/0a21eb4c57750729a48b88e9bbdb987eb6250a5bc2157b59316f5f1887db5"),
		MINUS_WOOD("http://textures.minecraft.net/texture/bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193"),
		MINUS_STONE("http://textures.minecraft.net/texture/a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611"),
		
		NEXT_WOOD("http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"),
		PREVIOUS_WOOD("http://textures.minecraft.net/texture/b6e14f2b7d1f5cb6f56ab3e6881fc8490eda6ff2d1d48a3241d147223cb3"),
		CLOSE_WOOD("https://textures.minecraft.net/texture/5b30507783c37db3a3092cad043e57951aa8b4c6ea9acc47d604b7eb5aea028");
		
		PlayerHead(String url) { this.url = url; }
		
		public void loadHead() { heads.put(this, getHead0(url, "name", new ArrayList<>())); }
		
		public ItemStack getHead(String name, List<String> lore) {
			
			if(heads.get(this) == null) loadHead();
			ItemStack item = heads.get(this).clone();
			
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name.replace("&", "\u00a7"));
			if(lore != null) {
				for(String s : lore) lore.set(lore.indexOf(s), s.replace("&", "\u00a7"));
				meta.setLore(lore);
			}
			item.setItemMeta(meta);
			
			return item;
			
		}
		
		public ItemStack getHead() {
			
			ItemStack item = heads.get(this).clone();
			if(item == null) {
				loadHead();
				item = heads.get(this).clone();
			}
			
			return item;
			
		}
		
		private String url;
		
	}
	
	public static boolean needUpdate() { return need_update; }
	public static boolean inform() { return inform; }
	
	public static String updateLink() { return instance.updater.getResourceURL(); }
	
	private boolean piston_fix = false, explosion_fix = false;
	public boolean pistonFix() { return piston_fix; }
	
	public boolean explosionFix() { return explosion_fix; }
	
	public String getVaultPrice(double d) {
		String s = "";
		if(eco != null) s = " " + eco.currencyNameSingular();
		if(s.equalsIgnoreCase("  ") || s.equalsIgnoreCase(" ")) s = "";
		return new DecimalFormat("#.##").format(d) + s;
	}
	
}
