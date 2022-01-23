package me.DenBeKKer.ntdLuckyBlock.util.manager;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.configuration.file.FileConfiguration;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;

public class MessagesManager {
	
	private static HashMap<Message, Object> map = new HashMap<>();
	private final static Collection<String> build_in_languages = Arrays.asList("en", "ru", "zh_cn", "de", "pl", "pt_br", "tr");
	private static Config config = null;
	public static File lang_folder = new File(LBMain.getInstance().getDataFolder() + File.separator + "lang");
	
	public enum Message {
		
		WATER_BUCKET("special.water_bucket"),
		LOADING_CONFIG("system.loading_config"),
		RELOADED_CONFIG("system.reloaded_config"),
		MATERIAL_API("system.material_api"),
		CRAFT_ALLOWED_ONE_LAYER("craft.allowed_one_layer"),
		CI_NOT_FOUND("custom_items.item_not_found"),
		CI_MAGIC_WOOL("custom_items.magic_wool"),
		CI_SWORD_OF_JUSTICE("custom_items.sword_of_justice"),
		CI_AXE_OF_PERUN("custom_items.axe_of_perun"),
		CI_CHILLY_PANTS("custom_items.chilly_pants"),
		CI_RAGE_ARMOR("custom_items.rage_armor"),
		CI_CARROT_CORRUPTER("custom_items.carrot_corrupter"),
		CI_MYSTERY_MEAT("custom_items.mystery_meat"),
		CI_WITHER_BLAST_ROD("custom_items.wither_blast_rod"),
		CI_LIST("custom_items.list"),
		LB_NOT_FOUND("system.lb_not_found"),
		LB_LOADING_EXCEPTION("system.lb_loading_exception"),
		LB_UNKNOWN_EXCEPTION("system.lb_unknown_exception"),
		LB_LOADED_ZERO("system.lb_loaded_zero"),
		LB_LOADED_NOT_ZERO("system.lb_loaded_not_zero"),
		CANT_USE_ANY("system.cant_use_command"),
		CANT_USE_SUB("system.cant_use_subcommand"),
		CANT_INTERACT_WORLD("system.cant_interact_world"),
		CMD_LB_DISABLED("system.cmd_lb_disabled"),
		CMD_LB_NOT_FOUND("system.cmd_lb_not_found"),
		CMD_LB_RECEIVED("system.cmd_lb_received"),
		CMD_CI_RECEIVED("system.cmd_ci_received"),
		CMD_LB_GIVE("system.cmd_lb_give"),
		CMD_PLAYER_NOT_FOUND("system.cmd_player_not_found"),
		GUI_GET_TITLE("gui.get.title"),
		GUI_COUNT_TITLE("gui.count.title"),
		GUI_COUNT_REMOVE("gui.count.remove"),
		GUI_COUNT_BACK("gui.count.back"),
		GUI_COUNT_CONFIRM("gui.count.confirm"),
		GUI_COUNT_GET("gui.count.get"),
		GUI_COUNT_GIVE("gui.count.give"),
		GUI_GET_NOT_MONEY("gui.get.no_money"),
		GUI_GET_SUCCESS("gui.get.success"),
		GUI_COUNT_ADD("gui.count.add"),
		GUI_EXIT("gui.exit"),
		CMD_NO_PERM_TO_COLOR("system.cmd_no_perm_to_color"),
		CMD_NO_PERM("system.cmd_no_perm"),
		CANT_BREAK_LUCKYBLOCK("system.cant_break_color"),
		CMD_HELP("system.cmd.help_title"),
		CMD_GET("system.cmd.get"),
		CMD_CUSTOMITEMGET("system.cmd.customitemget"),
		CMD_GIVE("system.cmd.give"),
		CMD_GUI("system.cmd.gui"),
		CMD_ITEMINFO("system.cmd.iteminfo"),
		CMD_LIST("system.cmd.list"),
		CMD_CUSTOMITEMSLIST("system.cmd.customitemslist"),
		CMD_RELOAD("system.cmd.reload"),
		CMD_SUPPORT("system.cmd.support"),
		CMD_CHECKFORUPDATES("system.cmd.checkforupdates"),
		CMD_DESTROY("system.cmd.destroy"),
		CMD_DESTROYED_LB("system.cmd_destroyed_lb");
		
		private String path;
		
		private Message(String string) {
			this.path = string;
		}
		
		@Deprecated
		public String get() { return getAsString(true); }
		
		@Deprecated
		public String get(boolean b) { return getAsString(b); }
		
		public Object getAsObject() { return map.containsKey(this) ? map.get(this) : "\u00a7c<<Translation for path \u00a7e<" + this.path + ">\u00a7c is missed>>"; }
		
		public String getAsString() { return getAsString(true); }
		
		public String getAsString(boolean formated) {
			return (map.containsKey(this) && map.get(this) != null) ? (formated ? Misc.setColors((String) map.get(this)) : (String)map.get(this))
					: "\u00a7c<<Translation for path \u00a7e<" + this.path + ">\u00a7c is missed>>";
		}
		
		public static void loadAll() {
			for(Message msg : Message.values()) msg.load();
			config.gc(false, true);
		}
		
		private void load(boolean copy_path) {

			map.put(this, config.get().get(this.path));
			
			if(copy_path && map.get(this) == null) {
				LBMain.log(Level.WARNING, "Translation for path <" + this.path + "> not found. Creating...");
				updateLocalePath(config, path);
				load(false);
			}
			
		}
		
		private void load() { load(true); }
		
		public List<String> getAsList(boolean formated) {
			return formated ? getAsList().stream().map(n -> Misc.setColors(n)).collect(Collectors.toList()) : getAsList();
		}
		
		@SuppressWarnings("unchecked")
		public List<String> getAsList() {
			if(map.containsKey(this)) {
				if(map.get(this) instanceof String)
					return Arrays.asList((String)map.get(this));
				else return (List<String>)map.get(this);
			}
			return Arrays.asList("\u00a7c<<Translation for path \u00a7e<" + this.path + ">\u00a7c is missed>>");
		}
		
	}
	
	/*
	 * @deprecated
	 * <p> Use {@link MessagesManager#getBuildInLanguages()} instead.
	 */
	@Deprecated
	public static Collection<String> getLanguages() { return getBuildInLanguages(); }
	
	public static Collection<String> getBuildInLanguages() {
		return build_in_languages;
	}
	
	public static Collection<String> getActualLanguages() {
		return Stream.of(lang_folder.listFiles()).map(n -> n.getName())
				.filter(n -> n.endsWith(".yml")).map(n -> n.toLowerCase().substring(0, n.length() - 4)).collect(Collectors.toList());
	}
	
	public static String getLanguage() {
		if(config == null) return null;
		return config.getName().split("\\.")[0];
	}
	
	public static void reload(String lang) {
		
		for(String language : build_in_languages)
			new Config(LBMain.getInstance(), "configuration.lang", lang_folder, language + ".yml").copy(false);
		
		if(lang == null) lang = "en";
		File translation = new File(lang_folder + File.separator + lang + ".yml");
		if(!translation.exists()) {
			lang = "en";
			
			translation = new File(lang_folder + File.separator + lang + ".yml");
			
		}

		if(!lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("ru"))
			MvLogger.log(Level.WARNING, "Loading unverified language file! It may contains exceptions or illegal words. Check before using is recommended");
		
		if(translation.exists()) {
			config = new Config(LBMain.getInstance(), "configuration.lang", lang_folder, lang + ".yml");
			config.copy(true);
			
			if(config.get().getString("author") == null) {
				config.get().set("author", "Unknown");
				config.save();
			}
			
			LBMain.log(Level.INFO, "Loading language file \"" + lang + "\" by " + config.get().getString("author"));
			Message.loadAll();
		} else LBMain.log(Level.SEVERE, "Reset translation config was not found!");
		
	}
	
	public static void updateLocalePath(Config config, String path) {
		
		FileConfiguration file = config.getName().contains("custom") ? null : config.getDefault();
		if(file != null && file.isSet(path)) {
			config.get().set(path, file.get(path));
		} else {
			LBMain.log(Level.WARNING, "Translation path <" + path + "> not found for language " + config.getName() + ", using english!");
			config.get().set(path, new Config(LBMain.getInstance(), "configuration.lang", lang_folder, "en.yml").getDefault().get(path));
		}
		
		config.save();
		
	}
	
}
