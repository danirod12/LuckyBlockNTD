package me.DenBeKKer.ntdLuckyBlock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.command.CommandsManager;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.economy.EconomyBridge;
import me.DenBeKKer.ntdLuckyBlock.economy.TokenManagerEconomy;
import me.DenBeKKer.ntdLuckyBlock.economy.VaultEconomy;
import me.DenBeKKer.ntdLuckyBlock.factory.LBFactory;
import me.DenBeKKer.ntdLuckyBlock.loader.ConvertManager;
import me.DenBeKKer.ntdLuckyBlock.recipe.CraftListener;
import me.DenBeKKer.ntdLuckyBlock.recipe.LuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.sk89q.LBWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.sk89q.LBWorldGuard;
import me.DenBeKKer.ntdLuckyBlock.util.ColorData;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Metrics;
import me.DenBeKKer.ntdLuckyBlock.util.SpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;
import me.DenBeKKer.ntdLuckyBlock.util.material.TintedMaterial;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.variables.WorldsList;

public class LBMain extends JavaPlugin {
	
	private static LBMain instance;
	public Config config;
	public IMat factory;
	private static boolean inform;
	private static File folder, schematics_folder;
	private static String version;
	private EconomyBridge economy_bridge;
	public SpigotUpdater updater;
	public WorldsList w;
	
	private static boolean reduce = false,
			brperm = true,
			debug = false,
			schematics = false,
			h = true,
			warning_luckyblock_changed = false;
	
	public boolean disable_author_info = false;
	public boolean web_unavailable_disable = false;
	public boolean reduce_convert = false;
	private ConvertManager convert_manager = new ConvertManager();
	private CommandsManager commands_manager;
	private static String NMS_VERSION;
	public static String getNMSVersion() { return NMS_VERSION; }
	
	
	// Last update date & Build number
	private static final String last_update = "27/11/2021";
	private static final int build = 69;
	// Last update date & Build number
	
	
	private static final boolean premium = false;
	public static boolean isPremium() { return premium; }
	
	public static LBMain getInstance() { return instance; }
	public static File getFolder() { return folder; }
	public static String getVersion() { return version; }
	public static String getLastUpdate() { return last_update; }
	public static int getBuild() { return build; }
	public static File getSchematicsFolder() { return schematics_folder; }
	public static boolean getIsSk89q() { return schematics; }
	public static boolean isReduced() { return reduce; }
	public static boolean h() { return h; }
	public static EconomyBridge getEconomy() { return instance.economy_bridge; }
	public static boolean isBreakPermissions() { return brperm; }
	@Deprecated
	public static boolean isVerifyName() { return false; }
	@Deprecated
	public static boolean isVerifyUUID() { return getConvertManager().isVerifyUUID(); }
	@Deprecated
	public static boolean isVerifyTAG() { return getConvertManager().isVerifyTAG(); }
	public static boolean warning_luckyblock_changed() { return warning_luckyblock_changed; }
	public static CommandsManager getCommandsManager() { return instance.commands_manager; }
	public static ConvertManager getConvertManager() { return instance.convert_manager; }
	
	public static boolean isDebug() { return debug; }
	@Deprecated
	public static boolean getDebug() { return debug; }
	
	public static Class<?> getClass(String path) {
		try {
			return Class.forName(path);
		} catch(Throwable th) { return null; }
	}
	
	public static UUID getUUID(ItemStack item) {
		
		if(item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		
		if(!(meta instanceof SkullMeta)) return null;
		
		SkullMeta smeta = (SkullMeta) meta;
		
		if(debug) debug("Getting GameProfile");
        try {
        	
        	Field profileField = smeta.getClass().getDeclaredField("profile");
			
	        profileField.setAccessible(true);
	        
	        GameProfile profile = (GameProfile)profileField.get(smeta);
	        return profile == null ? null : profile.getId();
			
		} catch (Exception e) {
			e.printStackTrace();
	        return null;
		}
        
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
		
		NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName();
		NMS_VERSION = NMS_VERSION.substring(NMS_VERSION.lastIndexOf('.') + 1);
		
		instance = this;
		updater = new SpigotUpdater(instance, 92026, "LuckyBlock");
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
		metrics.addCustomChart(new Metrics.SimplePie("language", () -> {
			final String language = MessagesManager.getLanguage();
			return language == null ? "undefined" : language;
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
		log(Level.INFO, ChatColor.DARK_GRAY + " > " + ChatColor.AQUA + "Discord " + ChatColor.WHITE + "- " + getDiscordURL());
		log(Level.INFO, ChatColor.DARK_GRAY + " > " + ChatColor.BLUE + "Vkontakte "
				+ ChatColor.WHITE + "- https://vk.com/danirodplay " + ChatColor.GRAY + "(Rus)");
		log(Level.INFO, ChatColor.WHITE + "=-= " + ChatColor.GOLD + "SUPPORT & BUG REPORTING & FEATURE REQUESTING " + ChatColor.WHITE + "=-=");
		if(ThreadLocalRandom.current().nextBoolean())
			log(Level.INFO, ChatColor.RED + "Help me make my plugin better! Fill out " + ChatColor.GOLD + "https://forms.gle/GvX5pB4BXU7BhAEi9");
		
		loadConfig();
		
//		checkForUpdates();
		if(config.get().getBoolean("scheduled-update-check")) {
			debug("Loading Bukkit scheduler thread for delayed update check");
			Bukkit.getScheduler().runTaskTimer(instance, () -> checkForUpdates(), 100L, 72000L);
		} else Bukkit.getScheduler().runTaskLater(instance, () -> checkForUpdates(), 100L);
		
		if(web_unavailable_disable)
			log(Level.INFO, "\"Web-Server is unavailable\" message is disabled. Plugin page - " + updater.getResourceURL());
		
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
		
		if(Hooks.TokenManager.isEnabled()) {
			
			Config token = new Config(instance, "configuration.other", null, "token_manager");
			token.copy(true);
			
			if(token.get().getBoolean("override-vault")) {
				this.economy_bridge = new TokenManagerEconomy(token.get().getString("display"));
			} else Hooks.TokenManager.disable("disabled in config");
			
		}
		
		if(!Hooks.TokenManager.isEnabled() && Hooks.Vault.isEnabled()) {
			this.economy_bridge = new VaultEconomy();
		}
		
		schematics_folder = new File(getDataFolder(), "schematics");
		if(Hooks.WorldEdit.isEnabled()) {
			debug("Loading WorldEdit provider...");
			
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
				debug("Hooked into " + (LBWorldEdit.isFAWE() ? "FastAsync" : "") + "WorldEdit");
				
			}
			
			if(Hooks.WorldGuard.isEnabled() && !LBWorldGuard.isAvailable())
				Hooks.WorldGuard.disable("unsupported version");
			
		}
		
		debug("Loading custom item factory...");
		try {
			CustomItemFactory.loadSystem();
		} catch(Throwable th) {
			th.printStackTrace();
		}
		
		Hooks.print();
		system_load();
		
		try {
			GuiManager.init();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		debug("Loading event managers...");
		Bukkit.getPluginManager().registerEvents(new LBHandler(), this);
		Bukkit.getPluginManager().registerEvents(new GuiManager(), this);
		Bukkit.getPluginManager().registerEvents(new CraftListener(), this);
		Bukkit.getPluginManager().registerEvents(convert_manager, this);
		
		Bukkit.getScheduler().runTaskLater(instance, () -> {
			
			log(Level.INFO, "If you are love my plugin, support me with 5 stars review (https://www.spigotmc.org/resources/92026/) or consider " +
					"to purchase premium plugin version (https://www.spigotmc.org/resources/94872/)");
			Bukkit.getOnlinePlayers().stream().filter(n -> n.hasPermission("luckyblock.supportme")).forEach(n -> {
				n.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7eIf you are love my plugin, support me with \u00a765 stars review"
						+ " \u00a7e(\u00a7b https://www.spigotmc.org/resources/92026/ \u00a7e) or consider to purchase premium plugin" +
						" version \u00a7e( \u00a7bhttps://www.spigotmc.org/resources/94872/ \u00a7e)");
			});
			
		}, 120);
		
		debug("Loading commands manager...");
		commands_manager = new CommandsManager();
		PluginCommand command = Bukkit.getPluginCommand("ntdluckyblock");
		command.setExecutor(commands_manager);
		command.setTabCompleter(commands_manager);
		
		ms = (System.currentTimeMillis() - ms);
		log(Level.INFO, ChatColor.GREEN + "Enabled " + msIndicator(ms, 1000, 3000) + "(took " + ms + " ms)" + ChatColor.GREEN + "... ");
		if(ms > 5000) log(Level.INFO, "\u00a7c\u00a7l[!] \u00a7ePlugin initialization took over 5 second (" + ms + " ms)");
		
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
			if(config.get().getString("config-level").equalsIgnoreCase("1.6")) {
				getLogger().log(Level.INFO, "Your config level is 1.6, updating to 1.7...");
				config.get().set("disable-author-info-gui-get", false);
				config.get().set("config-level", "1.7");
				config.save();
			}
			if(config.get().getString("config-level").equalsIgnoreCase("1.7")) {
				
				warning_luckyblock_changed = true;
				
				getLogger().log(Level.INFO, "Your config level is 1.7, updating to 1.8...");
				config.get().set("place.verify-name", true);
				config.get().set("place.verify-UUID", true);
				config.get().set("config-level", "1.8");
				config.save();
			}
			if(config.get().getString("config-level").equalsIgnoreCase("1.8")) {
				getLogger().log(Level.INFO, "Your config level is 1.8, updating to 1.9...");
				config.get().set("web-unavailable-disable", false);
				config.get().set("config-level", "1.9");
				config.save();
			}
			if(config.get().getString("config-level").equalsIgnoreCase("1.9")) {
				getLogger().log(Level.INFO, "Your config level is 1.9, updating to 1.10...");
				config.get().set("prevent-hat-luckyblocks", true);
				config.get().set("config-level", "1.10");
				config.save();
			}
			if(config.get().getString("config-level").equalsIgnoreCase("1.10")) {
				getLogger().log(Level.INFO, "Your config level is 1.10, updating to 1.11...");
				config.get().set("disable-json-convert-checking", false);
				config.get().set("skip-factory-broken", true);
				config.get().set("config-level", "1.11");
				config.save();
			}
		}
		
		brperm = config.get().getBoolean("break-permissions");
		reduce = config.get().getBoolean("reduce-command-author-info");
		reduce_convert = config.get().getBoolean("disable-json-convert-checking");
		h = config.get().getBoolean("skip-factory-broken");
		p$s = config.get().getBoolean("prevent-hat-luckyblocks");
		
		if(config.get().isSet("place.verify-name")) {
			
			config.get().set("place.verify-name", null);
			config.get().set("place.verify-TAG", true);
			config.get().set("place.convert-factory", true);
			config.save();
			log(Level.WARNING, "Old storage scheme found!");
			log(Level.WARNING, "  > disabling name checking");
			log(Level.WARNING, "  > enabling tag factory converter");
			log(Level.WARNING, "THIS MAY CAUSE BAD PERFOMANCE IMPACT!");
			
		}
		
		convert_manager.options(config.get().getBoolean("place.verify-UUID"), config.get().getBoolean("place.verify-TAG"));
		convert_manager.toggleFactory(config.get().getBoolean("place.convert-factory"));
		
		if(convert_manager.isVerifyTAG() == false && convert_manager.isVerifyUUID() == false)
			log(Level.WARNING, "You must enable place.verify-TAG or place.verify-UUID option for plugin work");
		
		MessagesManager.reload(config.get().getString("language"));
		
		debug = config.get().getBoolean("debug");
		if(debug) getLogger().log(Level.WARNING, "Debug mode enabled! Note that this mode only for developer!");
		inform = config.get().getBoolean("inform-about-update");
		
		disable_author_info = config.get().getBoolean("disable-author-info-gui-get") || config.get().getBoolean("disable-author-info");
		if(disable_author_info && !premium) {
			disable_author_info = false;
			
			log(Level.WARNING, "Sorry, but you cant disable author info for gui get command in free plugin version.");
			log(Level.WARNING, "Check out premium plugin version - https://www.spigotmc.org/resources/94872");
			
		}
		
		web_unavailable_disable = config.get().getBoolean("web-unavailable-disable");
		
		Config worlds = new Config(this, "configuration.other", null, "worlds");
		worlds.copy(true);
		w = new WorldsList(worlds.get().getString("mode"), worlds.get().getStringList("list"));
		w.setBreakNoDrop(worlds.get().getBoolean("break-no-drop"));
		w.setPlaceAdmins(worlds.get().getBoolean("place-admins"));
		try {
			CustomItemFactory.reloadSystem();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	public static void debug(String string) { if(debug) log(Level.INFO, "[DEBUG] " + string); }
	
	public void system_load() {
		
		long ms = System.currentTimeMillis();
		debug("Loading system...");
		map.clear();
		
		if(commands_manager != null)
			commands_manager.fix_ram(null);
		
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
	
	@Deprecated
	public void checkForUpdates() { checkForUpdates(false); }
	
	public static void checkForUpdates(boolean inform) { getUpdater().check$announce(!instance.web_unavailable_disable || debug, LBMain.inform || inform); }
	
	private static HashMap<LuckyBlockType, LuckyBlock> map = new HashMap<>();
	
	public enum Hooks {
		
		Vault,
		TokenManager,
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
		
		public static Hooks parse(String name) {
			for(Hooks hook : values())
				if(hook.name().equalsIgnoreCase(name)) return hook;
			return null;
		}
		
	}
	
	public enum LuckyBlockType {
		
		TINTED("a57a8c5ef6cafefa50eb308b97a6375b132def6fb6c50b107fbb39a28fa6c227"),
		ICED("2fbe5d2c82ef397513cd757f7735e653238fb62de672907e6b4762a1767afa7d"),
		
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
		
		private final String texture;
		
		LuckyBlockType(String texture) {
			this.texture = texture;
		}
		
		@Deprecated
		public DyeColor asDye() {
			
			if(this == LuckyBlockType.LIGHT_GRAY && instance.factory instanceof Mat1_12)
				return DyeColor.valueOf("SILVER");
			if(this == LuckyBlockType.TINTED) return DyeColor.BLACK;
			if(this == LuckyBlockType.ICED) return DyeColor.LIGHT_BLUE;
			
			return DyeColor.valueOf(this.name());
			
		}
		
		public ColorData asColor() {
			
			if(this == LuckyBlockType.TINTED) return ColorData.BLACK;
			if(this == LuckyBlockType.ICED) return ColorData.LIGHT_BLUE;
			return ColorData.valueOf(name());
			
		}
		
		public static LuckyBlockType parse(ColorData data) {
			return LuckyBlockType.valueOf(data.name());
		}
		
		public static LuckyBlockType parse(String name) {
			for(LuckyBlockType type : values())
				if(type.name().equalsIgnoreCase(name)) return type;
			return null;
		}
		
		public static LuckyBlockType parse(UUID uuid) {
			for(LuckyBlockType type : values())
				if(type.getUUID().equals(uuid)) return type;
			return null;
		}
		
		public boolean isAvailable() {
			
			if(this == LuckyBlockType.TINTED) return TintedMaterial.isAvailable();
			if(this == LuckyBlockType.ICED) return isPremium();
			
			return true;
			
		}
		
		public String load() {
			
			if(!isAvailable()) return "LuckyBlock \"" + name() + "\" is not available";
			
			Config config = new Config(instance, "configuration.luckyblocks." + instance.factory.build(), folder, this.name().toLowerCase() + ".yml");
			
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
			block.loadRecipes();
			return null;
			
		}
		
		public Material getMaterial() {
			if(this == LuckyBlockType.TINTED) return TintedMaterial.getMaterial();
			if(this == LuckyBlockType.ICED) return Material.ICE;
			return instance.factory.getGlass(asColor(), 1).getType();
		}
		
		@Deprecated
		public ItemStack getItem() {
			return instance.factory.getGlass(asColor(), 1);
		}
		
		public boolean isLoaded() { return map.get(this) != null; }
		
		public LuckyBlock get() throws LuckyBlockNotLoadedException {
			LuckyBlock lb = map.get(this);
			if(lb == null) throw new LuckyBlockNotLoadedException(this);
			return lb;
		}
		
		public static Set<LuckyBlockType> enabled() { return map.keySet(); }
		@Deprecated
		public static Set<LuckyBlockType> list() { return enabled(); }
		
		public static HashMap<LuckyBlockType, LuckyBlock> map() { return map; }
		
		@Deprecated
		public String fixName(String name) {
			
			try {
				while(name.length() >= 2 && (name.charAt(name.length() - 2) == '&' || name.charAt(name.length() - 2) == '\u00a7')) {
					name = name.substring(0, name.length() - 2);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			String name0 = name + "\u00a7" + toColorSymbol();
			debug(("Remmaped " + name + " to " + name0 + " to avoid same names").replace("\u00a7", "&"));
			return name0;
			
		}
		
		public String getCustomName(boolean colored) {
			return (colored ? "\u00a7" + toColorSymbol() : "") + (map.containsKey(this) ? map.get(this).getCustomName() : this.name());
		}
		
		@Deprecated
		public String getCustomName() { return getCustomName(false); }
		
		public String getTexture() { return this.texture; }
		
		public UUID getUUID() {
			if(this == LuckyBlockType.TINTED) return UUID.fromString("12345678-1234-1234-1234-000000000010");
			if(this == LuckyBlockType.ICED) return UUID.fromString("12345678-1234-1234-1234-000000000011");
			return UUID.fromString("12345678-1234-1234-1234-00000000000" + Integer.toHexString(asColor().getData()));
		}
		
		public String toColorSymbol() { return toColor(asColor().getData()); }
		
		public static LuckyBlockType random(boolean loaded) {
			
			LuckyBlockType[] types = loaded ?
					LuckyBlockType.map().keySet().toArray(new LuckyBlockType[LuckyBlockType.map().size()]) : LuckyBlockType.values();
			
			if(types.length == 0)
				types = LuckyBlockType.values();
			
			return types[ThreadLocalRandom.current().nextInt(types.length)];
			
		}
		
		public Collection<LuckyRecipe> getRecipes() throws LuckyBlockNotLoadedException {
			
			return get().getRecipes();
			
		}
		
		public boolean isColoredGlass() {
			return this != TINTED && this != ICED;
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
	private static boolean p$s;
	
	public static ItemStack getHead0(String url, String name, List<String> lore) { return getHead0(url, name, lore, UUID.randomUUID()); }
	
	public static ItemStack getHead0(String url, String name, List<String> lore, UUID uuid) {
		if(url == null || url.isEmpty())
			throw new UnsupportedOperationException("URL cannout be null");
		ItemStack head = instance.factory.getItem(Mat.PLAYER_SKULL, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(uuid, null);
        profile.getProperties().put("textures", new Property("textures",
        		new String(Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()))));
        try {
        	Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(name != null) headMeta.setDisplayName(name);
        if(lore != null) headMeta.setLore(lore);
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
	
	@Deprecated
	public static boolean inform() { return inform; }
	public static boolean isInformAbountUpdates() { return inform; }
	
	@Deprecated
	public static String updateLink() { return instance.updater.getResourceURL(); }
	
	public static boolean isPreventSkulls() { return p$s; }
	public static SpigotUpdater getUpdater() { return instance.updater; }
	
	public static final String getDiscordURL() { return "https://discord.gg/vbYW3sperj"; }
	
}
