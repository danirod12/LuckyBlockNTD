package me.DenBeKKer.ntdLuckyBlock.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class MessagesManager {
	
	private static HashMap<Message, Object> map = new HashMap<>();
	private static Config config = null;
	public static File lang_folder = new File(LBMain.getInstance().getDataFolder() + File.separator + "lang");
	
	public enum Message {
		
		WATER_BUCKET("special.water_bucket"),
		LOADING_CONFIG("system.loading_config"),
		RELOADED_CONFIG("system.reloaded_config"),
		MATERIAL_API("system.material_api"),
		LB_NOT_FOUND("system.lb_not_found"),
		LB_LOADING_EXCEPTION("system.lb_loading_exception"),
		LB_UNKNOWN_EXCEPTION("system.lb_unknown_exception"),
		LB_LOADED_ZERO("system.lb_loaded_zero"),
		LB_LOADED_NOT_ZERO("system.lb_loaded_not_zero"),
		CANT_USE_ANY("system.cant_use_command"),
		CANT_USE_SUB("system.cant_use_subcommand"),
		CMD_LB_DISABLED("system.cmd_lb_disabled"),
		CMD_LB_NOT_FOUND("system.cmd_lb_not_found"),
		CMD_LB_RECEIVED("system.cmd_lb_received"),
		CMD_LB_GIVE("system.cmd_lb_give"),
		CMD_PLAYER_NOT_FOUND("system.cmd_player_not_found"),
		GUI_GET_TITLE("gui.get.title"),
		GUI_COUNT_TITLE("gui.count.title"),
		GUI_COUNT_REMOVE("gui.count.remove"),
		GUI_COUNT_BACK("gui.count.back"),
		GUI_COUNT_CONFIRM("gui.count.confirm"),
		GUI_GET_NOT_MONEY("gui.get.no_money"),
		GUI_GET_SUCCESS("gui.get.success"),
		GUI_GET_EXCEPTION("gui.get.exception"),
		GUI_COUNT_ADD("gui.count.add"),
		GUI_EXIT("gui.exit"),
		CMD_NO_PERM_TO_COLOR("system.cmd_no_perm_to_color"),
		CANT_BREAK_LUCKYBLOCK("system.cant_break_color"),
		CMD_HELP("system.cmd_help");
		
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
			return (map.containsKey(this) && map.get(this) != null) ? (formated ? ((String)map.get(this)).replace("&", "\u00a7") : (String)map.get(this))
					: "\u00a7c<<Translation for path \u00a7e<" + this.path + ">\u00a7c is missed>>";
		}
		
		public static void loadAll() {
			for(Message msg : Message.values()) msg.load();
			config.deleteDefault();
			if(config.need_save())
				config.save();
		}
		
		private void load(boolean copy_path) {
			
//			String str = null;
//			try {
//				if(!(config.get().get(this.path) instanceof String)) {
//					for(String h : config.get().getStringList(this.path))
//						str += str == null ? h : str + "{newline}" + h;
//				}
//			} catch(Exception ex) {}
//			if(str == null) str = config.get().getString(this.path);
			map.put(this, config.get().get(this.path));
			
			if(copy_path && map.get(this) == null) {
				LBMain.log(Level.WARNING, "Translation for path <" + this.path + "> not found. Creating...");
				updateLocalePath(config, path);
				load(false);
			}
			
		}
		
		private void load() { load(true); }
		
		public List<String> getAsList(boolean formated) {
			return formated ? getAsList().stream().map(n -> n.replace("&", "\u00a7")).collect(Collectors.toList()) : getAsList();
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
	
	public static void reload(String lang) {
		
		new Config(LBMain.getInstance(), "configuration.lang", lang_folder, "en.yml").copy(false);
		new Config(LBMain.getInstance(), "configuration.lang", lang_folder, "ru.yml").copy(false);
		new Config(LBMain.getInstance(), "configuration.lang", lang_folder, "zh_cn.yml").copy(false);
		
		if(lang == null) lang = "en";
		File translation = new File(lang_folder + File.separator + lang + ".yml");
		if(!translation.exists()) {
			lang = "en";
			
			translation = new File(lang_folder + File.separator + lang + ".yml");
			
		}
		
		if(translation.exists()) {
			config = new Config(LBMain.getInstance(), lang_folder, lang + ".yml");
			config.copy(true);
			
			if(config.get().getString("author") == null) {
				config.get().set("author", "Unknown");
				config.need_save(true);
			}
			
			LBMain.log(Level.INFO, "Loading language file \"" + lang + "\" by " + config.get().getString("author"));
			Message.loadAll();
		} else LBMain.log(Level.SEVERE, "Reset translation config was not found!");
		
	}
	
	public static void updateLocalePath(Config config, String path) {
		
		FileConfiguration file = config.getName().contains("custom") ? null : config.getDefault(false, "configuration.lang");
		if(file != null && file.isSet(path)) {
			config.get().set(path, file.get(path));
		} else {
			LBMain.log(Level.WARNING, "Translation path <" + path + "> not found for language " + config.getName() + ", using english!");
			config.get().set(path, new Config(LBMain.getInstance(), "configuration.lang", lang_folder, "en.yml").getDefault(true, "configuration.lang").get(path));
		}
		
		config.need_save(true);
		
	}
	
}
