package com.github.danirod12.luckyblock.util.manager;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.util.Config;
import com.github.danirod12.luckyblock.util.Misc;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO rework. Remove static
public class MessagesManager {

    private static final Map<Message, Object> MAP = new HashMap<>();
    private static final List<String> BUILD_IN_LANGUAGES = Arrays.asList("en", "ru", "zh_cn", "de", "pl",
            "pt_br", "tr", "es", "fr", "it");
    public static File langFolder
            = new File(LuckyBlockAPI.getInstance().getDataFolder() + File.separator + "lang");
    private static Config config = null;

    public static Collection<String> getBuildInLanguages() {
        return BUILD_IN_LANGUAGES;
    }

    public static Collection<String> getActualLanguages() {
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        return Stream.of(langFolder.listFiles())
                .map(File::getName)
                .filter(n -> n.endsWith(".yml"))
                .map(n -> n.toLowerCase().substring(0, n.length() - 4))
                .collect(Collectors.toList());
    }

    public static String getLanguage() {
        if (config == null) {
            return null;
        }
        return config.getName().split("\\.")[0];
    }

    public static void reload(String lang) {

        for (String language : BUILD_IN_LANGUAGES) {
            new Config(LuckyBlockAPI.getInstance(),
                    "configuration.lang", langFolder, language + ".yml").copy(false);
        }

        if (lang == null) {
            lang = "en";
        }
        File translation = new File(langFolder + File.separator + lang + ".yml");
        if (!translation.exists()) {
            lang = "en";
            translation = new File(langFolder + File.separator + lang + ".yml");
        }

        if (!lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("ru")) {
            LuckyBlockAPI.getLogger().log(Level.WARNING, "Loading unverified language file! " +
                    "It may contains exceptions or illegal words. Check before using is recommended");
        }

        if (translation.exists()) {
            config = new Config(LuckyBlockAPI.getInstance(),
                    "configuration.lang", langFolder, lang + ".yml");
            config.copy(true);

            if (config.get().getString("author") == null) {
                config.get().set("author", "Unknown");
                config.save();
            }

            LuckyBlockAPI.getLogger().log(Level.INFO, "Loading language file \""
                    + lang + "\" by " + config.get().getString("author"));
            Message.loadAll();
        } else {
            LuckyBlockAPI.getLogger().log(Level.SEVERE, "Reset translation config was not found!");
        }
    }

    public static void updateLocalePath(Config config, String path) {
        FileConfiguration file = config.getName().contains("custom") ? null : config.getResourceConf();
        if (file != null && file.isSet(path)) {
            config.get().set(path, file.get(path));
        } else {
            LuckyBlockAPI.getLogger().log(Level.WARNING, "Translation path <" + path
                    + "> not found for language " + config.getName() + ", using english!");
            config.get().set(path, new Config(LuckyBlockAPI.getInstance(),
                    "configuration.lang", langFolder, "en.yml").getResourceConf().get(path));
        }
        config.save();
    }

    public enum Message {

        WATER_BUCKET("special.water_bucket"),
        LOADING_CONFIG("system.loading_config"),
        RELOADED_CONFIG("system.reloaded_config"),
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
        LB_PLACED("system.lb_placed"),
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
        WORLD_NOT_FOUND("system.world_not_found"),
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
        CMD_PLACE("system.cmd.place"),
        CMD_GUI("system.cmd.gui"),
        CMD_ITEMINFO("system.cmd.iteminfo"),
        CMD_LIST("system.cmd.list"),
        CMD_CUSTOMITEMSLIST("system.cmd.customitemslist"),
        CMD_RELOAD("system.cmd.reload"),
        CMD_SUPPORT("system.cmd.support"),
        CMD_CHECKFORUPDATES("system.cmd.checkforupdates"),
        CMD_DESTROY("system.cmd.destroy"),
        CMD_GENERATE("system.cmd.generate"),
        CMD_GENERATE_DESCRIPTION("system.cmd.generateFull"),
        CMD_DESTROYED_LB("system.cmd_destroyed_lb");

        @Getter
        private final String path;

        Message(String string) {
            this.path = string;
        }

        public static void loadAll() {
            for (Message msg : Message.values()) {
                msg.load();
            }
        }

        @Deprecated
        public String get() {
            return getAsString(true);
        }

        @Deprecated
        public String get(boolean formatted) {
            return getAsString(formatted);
        }

        public Object getAsObject() {
            return MAP.containsKey(this)
                    ? MAP.get(this) : "§c<<Translation for path §e<" + this.path + ">§c is missed>>";
        }

        public String getAsString() {
            return getAsString(true);
        }

        public String getAsString(boolean formatted) {
            return (MAP.containsKey(this) && MAP.get(this) != null)
                    ? (formatted ? Misc.setColors((String) MAP.get(this)) : (String) MAP.get(this))
                    : "§c<<Translation for path §e<" + this.path + ">§c is missed>>";
        }

        private void load(boolean copyPath) {
            MAP.put(this, config.get().get(this.path));

            if (copyPath && MAP.get(this) == null) {
                LuckyBlockAPI.getLogger().log(Level.WARNING,
                        "Translation for path <" + this.path + "> not found. Creating...");
                updateLocalePath(config, path);
                load(false);
            }
        }

        private void load() {
            load(true);
        }

        public List<String> getAsList(boolean formated) {
            return formated ? getAsList().stream().map(Misc::setColors).collect(Collectors.toList()) : getAsList();
        }

        @SuppressWarnings("unchecked")
        public List<String> getAsList() {
            if (MAP.containsKey(this)) {
                if (MAP.get(this) instanceof String) {
                    return Collections.singletonList((String) MAP.get(this));
                } else {
                    return (List<String>) MAP.get(this);
                }
            }
            return Collections.singletonList("§c<<Translation for path §e<" + this.path + ">§c is missed>>");
        }
    }
}
