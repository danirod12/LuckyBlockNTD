package me.DenBeKKer.ntdLuckyBlock;

import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.command.CommandsManager;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.economy.EconomyBridge;
import me.DenBeKKer.ntdLuckyBlock.economy.TokenManagerEconomy;
import me.DenBeKKer.ntdLuckyBlock.economy.VaultEconomy;
import me.DenBeKKer.ntdLuckyBlock.factory.LBFactory;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldGuard;
import me.DenBeKKer.ntdLuckyBlock.hook.thebusybiscuit.SlimeFunListener;
import me.DenBeKKer.ntdLuckyBlock.listener.CoreListener;
import me.DenBeKKer.ntdLuckyBlock.listener.CustomItemListener;
import me.DenBeKKer.ntdLuckyBlock.listener.LightSourceListener;
import me.DenBeKKer.ntdLuckyBlock.loader.ConvertManager;
import me.DenBeKKer.ntdLuckyBlock.nms.*;
import me.DenBeKKer.ntdLuckyBlock.recipe.CraftListener;
import me.DenBeKKer.ntdLuckyBlock.recipe.LuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.util.*;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.variables.PlayerHead;
import me.DenBeKKer.ntdLuckyBlock.variables.PluginVersion;
import me.DenBeKKer.ntdLuckyBlock.variables.WorldListDataHandler;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class LBMain extends JavaPlugin {

    private static LBMain instance;
    private static String NMS_VERSION;

    // General variables
    private static final Map<LuckyBlockType, LuckyBlock> map = new HashMap<>();
    private File folder;
    private File schematicsFolder = null;
    public Config config;
    public IMat factory;
    public Material tinted;
    public SpigotUpdater updater;
    public StringMatcher<WorldListDataHandler> worldsFilter;

    // Managers
    private EconomyBridge economy;
    private GuiManager guiManager;
    private ItemTag itemTagAdapter;
    private ConvertManager convertManager;
    private CommandsManager commandsManager;

    // Config fields
    private boolean debug = false;
    public boolean preventHatLB = true;
    public boolean breakPermissions = true;
    public boolean reduceAuthorInfo = false;
    public boolean disableAuthorInfo = false;
    public boolean informAboutUpdates = true;
    public boolean disableConvertCheck = false;
    public boolean disableWebIssuePrint = false;
    public boolean forceUpdateInventory = false;
    private boolean lightSource = false;

    public static boolean isPremium() {
        return Templates.VERSION.isPremium();
    }

    public static PluginVersion getVersionType() {
        return Templates.VERSION;
    }

    public static String getLastUpdate() {
        return Templates.COMPILE_DATE;
    }

    public static int getBuild() {
        return Templates.BUILD;
    }

    public static String getNMSVersion() {
        return NMS_VERSION;
    }

    public static LBMain getInstance() {
        return instance;
    }

    // LuckyBlocks folder
    public static File getFolder() {
        return instance.folder;
    }

    // Schematics folder or null if WorldEdit was not hooked
    public static File getSchematicsFolder() {
        return instance.schematicsFolder;
    }

    public static boolean isDebug() {
        return instance.debug;
    }

    public static void log(Level level, String message) {
        MvLogger.log(level, message);
    }

    public static void debug(String message) {
        if (isDebug()) log(Level.INFO, "[DEBUG] " + message);
    }

    public static String getDiscordURL() {
        return "https://discord.gg/vbYW3sperj";
    }

    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public SpigotUpdater getUpdater() {
        return updater;
    }

    public EconomyBridge getEconomyBridge() {
        return economy;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public ConvertManager getConvertManager() {
        return convertManager;
    }

    public ItemTag getItemTagAdapter() {
        return itemTagAdapter;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public boolean isLightSource() {
        return this.lightSource;
    }

    @Override
    public void onDisable() {
        if (this.guiManager != null)
            this.guiManager.close();
    }

    @Override
    public void onLoad() {
        LBWorldGuard.register();
    }

    @Override
    public void onEnable() {

        // data for onEnable
        long ms = System.currentTimeMillis();

        NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        // Init logger
        MvLogger.setInstance(instance = this);

        updater = new SpigotUpdater(instance, 92026, "LuckyBlock");
        folder = new File(instance.getDataFolder() + File.separator + "luckyblocks");
        folder.mkdirs();

        // Metrics
        Metrics metrics = new Metrics(this, 17081);
        metrics.addCustomChart(new Metrics.SimplePie("enabled_luckyblocks", () -> String.valueOf(LuckyBlockType.map().size())));
//        metrics.addCustomChart(new Metrics.SimplePie("luckydrop_types", () -> {
//            int a = 0, b = 0;
//            for (LuckyBlock z : LuckyBlockType.map().values()) {
//                a += z.items$mapped();
//                b += z.items$mappedTwice();
//            }
//            return a + "/" + b;
//
//        }));
        metrics.addCustomChart(new Metrics.SimplePie("version_type", Templates.VERSION::getSimpleName));
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> {
            final String language = MessagesManager.getLanguage();
            return language == null ? "undefined" : language;
        }));

        // Plugin title
        log(Level.INFO, ChatColor.WHITE + "Starting LuckyBlock (ntdLuckyBlock) v" + getVersion()
                + ", build" + getBuild() + "(" + getLastUpdate() + "), " + getVersionType().getSimpleName());
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

        // Material
        factory = Misc.getEnum(Material.class, "PLAYER_HEAD") == null ? new Mat1_12() : new Mat1_13();
        log(Level.INFO, "Loaded " + factory.build() + " material version");

        Material tinted = Misc.getEnum(Material.class, "TINTED_GLASS");
        this.tinted = tinted == null || !getVersionType().isPremium() ? null : tinted;

        // NMS
        log(Level.INFO, "Loading NMS for your platform (" + Bukkit.getVersion() + ", " + NMS_VERSION + ")...");
        if (!loadNMS()) return;

        // Before config
        convertManager = new ConvertManager();

        // Loading config
        reloadConfig();
        if (config.get().getBoolean("scheduled-update-check")) {
            debug("Loading Bukkit scheduler thread for delayed update check");
            Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                    () -> checkForUpdates(informAboutUpdates), 100L, 72000L);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance,
                    () -> checkForUpdates(informAboutUpdates), 100L);
        }

        if (disableWebIssuePrint)
            log(Level.INFO, "\"Web-Server is unavailable\" message is disabled. Plugin page - " + updater.getResourceURL());

        PlayerHead.loadAll();
        Hook.loadAll();

        // Economy
        if (Hook.TokenManager.isEnabled()) {

            Config token = new Config(instance, "configuration.other", null, "token_manager")
                    .copyMissedFields();

            if (token.get().getBoolean("override-vault")) {
                this.economy = new TokenManagerEconomy(token.get().getString("display"));
                log(Level.WARNING, "TokenManager is kinda beta feature. Report all bugs");
            } else Hook.TokenManager.disable("disabled via config");

        }

        if (this.economy != null && Hook.Vault.isEnabled()) {
            this.economy = new VaultEconomy();
        }

        // Schematics
        if (Hook.WorldEdit.isEnabled()) {
            schematicsFolder = new File(getDataFolder(), "schematics");
            debug("Loading WorldEdit provider...");

            if (!LBWorldEdit.isPlatformAvailable()) {
                Hook.WorldEdit.disable("unsupported version");
                schematicsFolder = null;
            } else {

                boolean legacy = factory.build().equalsIgnoreCase("old");
                if (!schematicsFolder.exists()) {
                    schematicsFolder.mkdirs();
                    new Config(instance, "configuration.schematics." + factory.build(), schematicsFolder,
                            "cage_lava.schem" + (legacy ? "atic" : "")).copy(false);
                    new Config(instance, "configuration.schematics.main", schematicsFolder,
                            "bedrock_problem.schematic").copy(false);
                    if (!legacy) new Config(instance, "configuration.schematics.main", schematicsFolder,
                            "small_temple.schematic").copy(false);
                }
                debug("Hooked into " + (LBWorldEdit.isFAWE() ? "FastAsync" : "") + "WorldEdit");

            }

            if (Hook.WorldGuard.isEnabled()) {
                debug("Loading WorldGuard provider...");
                if (!LBWorldGuard.isAvailable())
                    Hook.WorldGuard.disable("unsupported version");
            }

        }

        Hook.print();
        reloadSystem();

        // After system reload
        guiManager = new GuiManager();

        debug("Loading event managers...");
        if (Hook.SlimeFun.isEnabled()) Bukkit.getPluginManager().registerEvents(new SlimeFunListener(), this);
        Bukkit.getPluginManager().registerEvents(new CoreListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new CraftListener(), this);
        Bukkit.getPluginManager().registerEvents(guiManager, this);
        Bukkit.getPluginManager().registerEvents(convertManager, this);

        log(Level.INFO, "If you love my plugin, support me with 5 stars review" +
                "(https://www.spigotmc.org/resources/92026/) or consider to purchase " +
                "premium plugin version (https://www.spigotmc.org/resources/94872/)");

        debug("Loading commands manager...");
        commandsManager = new CommandsManager();
        PluginCommand command = Bukkit.getPluginCommand("ntdluckyblock");
        if (command == null) throw new UnsupportedOperationException("Command not found");
        command.setExecutor(commandsManager);
        command.setTabCompleter(commandsManager);

        ms = (System.currentTimeMillis() - ms);
        log(Level.INFO, "\u00a76Enabled " + Misc.getColorLevel(ms, 1000, 3000)
                + "(took " + ms + " ms)\u00a76... ");

    }

    private boolean loadNMS() {

        if (NMS_VERSION == null) return false;
        switch (NMS_VERSION) {

            case "v1_19_R2": {
                itemTagAdapter = new ItemTag1_19_R2();
                return true;
            }

            case "v1_19_R1": {
                itemTagAdapter = new ItemTag1_19_R1();
                return true;
            }

            case "v1_18_R2": {
                itemTagAdapter = new ItemTag1_18_R2();
                return true;
            }

            case "v1_18_R1": {
                itemTagAdapter = new ItemTag1_18_R1();
                return true;
            }

            default: {

                try {
                    itemTagAdapter = new ItemTagLegacy();
                    return true;
                } catch (UnsupportedOperationException ex) {
                    log(Level.WARNING, "Your platform is not supported. Supported versions 1.8 - 1.19.3");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return false;
                }

            }

        }

    }

    @Override
    public void reloadConfig() {
        boolean startup = config == null;

        config = new Config(instance, getDataFolder(), "config.yml").copy(true).copyMissedFields();

        forceUpdateInventory = config.getBoolean("force-update-inventory");
        disableConvertCheck = config.getBoolean("disable-json-convert-checking");
        informAboutUpdates = config.getBoolean("inform-about-update");
        breakPermissions = config.getBoolean("break-permissions");
        reduceAuthorInfo = config.getBoolean("reduce-command-author-info");
        preventHatLB = config.getBoolean("prevent-hat-luckyblocks");

        boolean lightSource = config.getBoolean("light-source");
        if (this.factory instanceof Mat1_12 && !lightSource) {
            this.getLogger().log(Level.WARNING, "Light source cannot be disabled on 1.8 - 1.12 " +
                    "due vanilla texture glitch (Texture is blacked out)");
            lightSource = true;
        }
        if (startup) {
            if (lightSource) {
                this.getLogger().log(Level.INFO, "[BETA FEATURE] Light source mode is set to true, injecting...");
                Bukkit.getPluginManager().registerEvents(new LightSourceListener(this), this);
                this.lightSource = true;
            }
        } else if (this.lightSource != lightSource) {
            this.getLogger().log(Level.SEVERE, "YOU CANNOT CHANGE LIGHT-SOURCE MODE WITHOUT RESTART");
            this.getLogger().log(Level.SEVERE, "RESTART IS REQUIRED TO APPLY CONFIG CHANGES");
        }

        if (config.isSet("place.verify-name")) {

            config.set("place.verify-name", null);
            config.set("place.convert-factory", true);
            config.save();
            log(Level.WARNING, "Old storage scheme found!");
            log(Level.WARNING, "  > disabling name checking");
            log(Level.WARNING, "  > enabling tag factory converter");
            log(Level.WARNING, "THIS MAY CAUSE BAD PERFORMANCE IMPACT!");

        }

        ConfigurationSection section = config.get().getConfigurationSection("chances");
        assert section != null;
        for (String key : section.getKeys(false)) {
            DropChance chance = DropChance.parse(key);
            if (chance == null) continue;
            chance.setWeight(section.getInt(key));
        }
        convertManager.setup(config.getBoolean("place.verify-UUID"), config.getBoolean("place.verify-TAG"));
        convertManager.setFactoryEnabled(config.getBoolean("place.convert-factory"));

        if (!convertManager.isVerifyTAG() && !convertManager.isVerifyUUID())
            log(Level.WARNING, "You must enable place.verify-TAG or place.verify-UUID option for plugin work");

        MessagesManager.reload(config.getString("language"));

        debug = config.getBoolean("debug");
        if (debug) getLogger().log(Level.WARNING, "Debug mode enabled! Note that this mode only for developer!");

        disableAuthorInfo = config.getBoolean("disable-author-info-gui-get")
                || config.getBoolean("disable-author-info");
        if (disableAuthorInfo && getVersionType().isFree()) {
            disableAuthorInfo = false;

            log(Level.WARNING, "Sorry, but you cant disable author info in free plugin version.");
            log(Level.WARNING, "Check out premium plugin version - https://www.spigotmc.org/resources/94872");

        }

        disableWebIssuePrint = config.getBoolean("web-unavailable-disable");

        Config worlds = new Config(this, "configuration.other", null, "worlds").copyMissedFields();
        worldsFilter = new StringMatcher<>(new WorldListDataHandler(worlds.getBoolean("break-no-drop"),
                worlds.getBoolean("place-admins")), worlds.getString("mode"), worlds.getStringList("list"));

    }

    public void reloadSystem() {

        long ms = System.currentTimeMillis();
        log(Level.INFO, "Loading system...");
        map.clear();

        // first time it will be null (On startup)
        if (commandsManager != null)
            commandsManager.gc(null);
        // first time it will be null (On startup)
        if (guiManager != null)
            guiManager.reload();

        for (String str : config.get().getStringList("enabled")) {
            try {
                LuckyBlockType lb = LuckyBlockType.parse(str);
                if (lb == null) {
                    log(Level.SEVERE, Message.LB_NOT_FOUND.getAsString().replace("%lb%", str.toUpperCase()));
                    continue;
                }
                String e = lb.load();
                if (e != null)
                    log(Level.SEVERE, Message.LB_LOADING_EXCEPTION.getAsString().replace("%lb%", str.toUpperCase()).replace("%exception%", e));
            } catch (Exception ex) {
                log(Level.SEVERE, Message.LB_UNKNOWN_EXCEPTION.getAsString().replace("%lb%", str.toUpperCase()));
                ex.printStackTrace();
            }
        }

        if (map.size() == 0) {
            log(Level.WARNING, Message.LB_LOADED_ZERO.getAsString());
        } else {
            log(Level.INFO, Message.LB_LOADED_NOT_ZERO.getAsString().replace("%amount%", String.valueOf(map.size())));
        }

        try {
            CustomItemFactory.loadSystem();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        log(Level.INFO, "System loaded (took " + (System.currentTimeMillis() - ms) + " ms)...");

    }

    public void checkForUpdates(boolean inform) {
        getUpdater().check$announce(debug || !disableWebIssuePrint, inform || informAboutUpdates);
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

        public static LuckyBlockType parse(ColorData data) {
            return LuckyBlockType.valueOf(data.name());
        }

        public static LuckyBlockType parse(String name) {
            for (LuckyBlockType type : values())
                if (type.name().equalsIgnoreCase(name)) return type;
            return null;
        }

        public static LuckyBlockType parse(UUID uuid) {
            for (LuckyBlockType type : values())
                if (type.getUUID().equals(uuid)) return type;
            return null;
        }

        public static Set<LuckyBlockType> enabled() {
            return map.keySet();
        }

        @Deprecated
        public static Set<LuckyBlockType> list() {
            return enabled();
        }

        public static Map<LuckyBlockType, LuckyBlock> map() {
            return map;
        }

        public static LuckyBlockType random(boolean loaded) {

            LuckyBlockType[] types = loaded && map.size() > 0 ? map.keySet().toArray(new LuckyBlockType[0])
                    : LuckyBlockType.values();
            return types[ThreadLocalRandom.current().nextInt(types.length)];

        }

        @Deprecated
        public DyeColor asDye() {

            if (this == LuckyBlockType.LIGHT_GRAY && instance.factory instanceof Mat1_12)
                return DyeColor.valueOf("SILVER");
            if (this == LuckyBlockType.TINTED) return DyeColor.BLACK;
            if (this == LuckyBlockType.ICED) return DyeColor.LIGHT_BLUE;

            return DyeColor.valueOf(this.name());

        }

        public String getLocatedName(Location location) {
            return this.name() + ";" + (int) location.getX() + ";"
                    + (int) location.getY() + ";" + (int) location.getZ();
        }

        public ColorData asColor() {

            if (this == LuckyBlockType.TINTED) return ColorData.BLACK;
            if (this == LuckyBlockType.ICED) return ColorData.LIGHT_BLUE;
            return ColorData.valueOf(name());

        }

        public boolean isAvailable() {

            if (this == LuckyBlockType.TINTED) return instance.tinted != null;
            if (this == LuckyBlockType.ICED) return isPremium();

            return true;

        }

        public Config getNewConfigInstance() {
            return new Config(instance, "configuration.luckyblocks." + instance.factory.build(),
                    instance.folder, this.name().toLowerCase() + ".yml");
        }

        public String load() {

            if (!isAvailable()) return "LuckyBlock \"" + name() + "\" is not available";

            Config config = getNewConfigInstance();
            if (!config.hasResource()) {
                config.write();
                config.reload();
                LBFactory.latest().generate(config, this, 20, 50);
                config.save();
            }

            config.copy(true);
            LuckyBlock block = new LuckyBlock(this, config);
            if (block.getException() != null) return block.getException();
            map.put(this, block);
            block.loadRecipes();
            return null;

        }

        public Material getMaterial() {
            if (this == LuckyBlockType.TINTED) return instance.tinted;
            if (this == LuckyBlockType.ICED) return Material.ICE;
            return instance.factory.getGlass(asColor(), 1).getType();
        }

        @Deprecated
        public ItemStack getItem() {
            return instance.factory.getGlass(asColor(), 1);
        }

        public boolean isLoaded() {
            return map.get(this) != null;
        }

        public LuckyBlock get() throws LuckyBlockNotLoadedException {
            LuckyBlock lb = map.get(this);
            if (lb == null) throw new LuckyBlockNotLoadedException(this);
            return lb;
        }

        @Deprecated
        public String fixName(String name) {

            try {
                while (name.length() >= 2 && (name.charAt(name.length() - 2) == '&' || name.charAt(name.length() - 2) == '\u00a7')) {
                    name = name.substring(0, name.length() - 2);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String name0 = name + "\u00a7" + toColorSymbol();
            debug(("Remapped " + name + " to " + name0 + " to avoid same names").replace("\u00a7", "&"));
            return name0;

        }

        public String getCustomName(boolean colored) {
            return (colored ? "\u00a7" + toColorSymbol() : "") + (map.containsKey(this) ? map.get(this).getCustomName() : this.name());
        }

        @Deprecated
        public String getCustomName() {
            return getCustomName(false);
        }

        public String getTexture() {
            return this.texture;
        }

        public UUID getUUID() {
            if (this == LuckyBlockType.TINTED) return UUID.fromString("12345678-1234-1234-1234-000000000010");
            if (this == LuckyBlockType.ICED) return UUID.fromString("12345678-1234-1234-1234-000000000011");
            return UUID.fromString("12345678-1234-1234-1234-00000000000" + Integer.toHexString(asColor().getData()));
        }

        public String toColorSymbol() {
            return this.asColor().asColorCode();
        }

        public Collection<LuckyRecipe> getRecipes() throws LuckyBlockNotLoadedException {

            return get().getRecipes();

        }

        public boolean isColoredGlass() {
            return this != TINTED && this != ICED;
        }

    }

}
