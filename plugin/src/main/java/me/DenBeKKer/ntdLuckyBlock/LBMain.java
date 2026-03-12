package me.DenBeKKer.ntdLuckyBlock;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.api.model.*;
import me.DenBeKKer.ntdLuckyBlock.api.provider.LBMainProvider;
import me.DenBeKKer.ntdLuckyBlock.api.util.*;
import me.DenBeKKer.ntdLuckyBlock.command.CommandsManager;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.hook.economy.EconomyBridge;
import me.DenBeKKer.ntdLuckyBlock.hook.economy.TokenManagerEconomy;
import me.DenBeKKer.ntdLuckyBlock.hook.economy.VaultEconomy;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.WorldEditProvider;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.WorldGuardProvider;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.WorldGuardProviderImpl;
import me.DenBeKKer.ntdLuckyBlock.hook.thebusybiscuit.SlimeFunListener;
import me.DenBeKKer.ntdLuckyBlock.listener.CoreListener;
import me.DenBeKKer.ntdLuckyBlock.listener.CustomItemListener;
import me.DenBeKKer.ntdLuckyBlock.listener.EntityLoadListener;
import me.DenBeKKer.ntdLuckyBlock.nms.VersionControlFactory;
import me.DenBeKKer.ntdLuckyBlock.recipe.CraftListener;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.SpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.util.StringMatcher;
import me.DenBeKKer.ntdLuckyBlock.util.Templates;
import me.DenBeKKer.ntdLuckyBlock.util.config.ConfigHolder;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import me.DenBeKKer.ntdLuckyBlock.variables.PlayerHead;
import me.DenBeKKer.ntdLuckyBlock.variables.world.WorldListDataHandler;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class LBMain extends LBMainProvider {

    // General variables
    public SpigotUpdater updater;
    @Getter
    private WorldEditProvider worldEditProvider;
    private WorldGuardProvider worldGuardProvider;
    public Single<StringMatcher<WorldListDataHandler>> worldsFilter = new Single<>();

    // Managers
    @Getter
    private VersionControlFactory versionControl;
    private LuckyBlockEngine luckyBlockEngine;
    private EconomyBridge economy;
    @Getter
    private GuiManager guiManager;
    @Getter
    private CommandsManager commandsManager;
    private EntityLoadListener entityLoadListener;

    @Getter
    private final ConfigHolder configHolder = new ConfigHolder();
    @Getter
    private final LogChannel logChannel = new LogChannel(this);

    public static boolean isPremium() {
        return Templates.VERSION.isPremium();
    }

    @Override
    public PluginVersion getVersionType() {
        return Templates.VERSION;
    }

    @Override
    public String getLastUpdate() {
        return Templates.COMPILE_DATE;
    }

    public EconomyBridge getEconomyBridge() {
        return economy;
    }

    public LuckyBlockEngine getEngine() {
        return luckyBlockEngine;
    }

    @Override
    public void onLoad() {
        // WorldGuard support should be injected on load stage
        if (JavaUtils.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin") != null
                && JavaUtils.getClass("com.sk89q.worldguard.protection.flags.Flag") != null) {
            this.worldGuardProvider = new WorldGuardProviderImpl(getLogger());
        }
    }

    @Override
    public void onEnable() {
        // some kind of timings ^_^
        long ms = System.currentTimeMillis();

        // Plugin title
        logChannel.info("§fStarting LuckyBlock (ntdLuckyBlock) v" + getVersion() + " [" + Templates.GIT_BRANCH + "-"
                + Templates.GIT_COMMIT_ID_ABBREV + "] (" + getLastUpdate() + "), " + getVersionType().getSimpleName());
        logChannel.info("§e  ╭╮╱╱╭╮╱╭┳━━━┳╮╭━┳╮╱╱╭┳━━╮╭╮╱╱╭━━━┳━━━┳╮╭━╮");
        logChannel.info("§e  ┃┃╱╱┃┃╱┃┃╭━╮┃┃┃╭┫╰╮╭╯┃╭╮┃┃┃╱╱┃╭━╮┃╭━╮┃┃┃╭╯");
        logChannel.info("§e  ┃┃╱╱┃┃╱┃┃┃╱╰┫╰╯╯╰╮╰╯╭┫╰╯╰┫┃╱╱┃┃╱┃┃┃╱╰┫╰╯╯");
        logChannel.info("§e  ┃┃╱╭┫┃╱┃┃┃╱╭┫╭╮┃╱╰╮╭╯┃╭━╮┃┃╱╭┫┃╱┃┃┃╱╭┫╭╮┃");
        logChannel.info("§e  ┃╰━╯┃╰━╯┃╰━╯┃┃┃╰╮╱┃┃╱┃╰━╯┃╰━╯┃╰━╯┃╰━╯┃┃┃╰╮");
        logChannel.info("§e  ╰━━━┻━━━┻━━━┻╯╰━╯╱╰╯╱╰━━━┻━━━┻━━━┻━━━┻╯╰━╯ NTD");
        logChannel.info("§f          Made with " + "§clove " + "§fby §a" + "danirod12");
        logChannel.info("");
        for (String entry : Templates.SUPPORT_INFO) {
            logChannel.info(entry);
        }

        // NMS & items
        try {
            this.versionControl = new VersionControlFactory(this.logChannel);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // At this stage we are sure that our plugin may run on current server

        // Updater & Metrics
        updater = new SpigotUpdater(this, 92026, "LuckyBlock");
        Metrics metrics = new Metrics(this, 17081);
        metrics.addCustomChart(new SimplePie("enabled_luckyblocks",
                () -> String.valueOf(getEngine().getLoadedTypes().length)));
        metrics.addCustomChart(new SimplePie("version_type", Templates.VERSION::getSimpleName));
        metrics.addCustomChart(new SimplePie("language", () -> {
            final String language = MessagesManager.getLanguage();
            return language == null ? "undefined" : language;
        }));

        // Before config
        guiManager = new GuiManager(this);
        File folder = new File(getDataFolder() + File.separator + "luckyblocks");
        luckyBlockEngine = new LuckyBlockEngine(this, logChannel, folder,
                configHolder, versionControl, worldEditProvider);
        entityLoadListener = new EntityLoadListener(this, luckyBlockEngine);
        LuckyBlockAPI.injectAPI(this, luckyBlockEngine);

        // Loading config
        reloadConfig();
        if (this.configHolder.getConfig().getBoolean("scheduled-update-check")) {
            logChannel.debug("Loading Bukkit scheduler thread for delayed update check");
            Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                    () -> checkForUpdates(this.configHolder.informAboutUpdates), 100L, 72000L);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this,
                    () -> checkForUpdates(this.configHolder.informAboutUpdates), 100L);
        }

        if (this.configHolder.webUnavailableDisable) {
            logChannel.info("\"Web-Server is unavailable\" message is disabled. " +
                    "Check for updates manually at plugin page - " + updater.getResourceURL());
        }

        // Init caches for textures player heads
        PlayerHead.loadAll(this.versionControl);
        // Check all hooks if related plugins exists
        Hook.loadAll();

        // Init economy
        if (Hook.TokenManager.isEnabled()) {
            Config token = new Config(this, "configuration.other", null, "token_manager")
                    .copyMissedFields();

            if (token.getBoolean("override-vault")) {
                this.economy = new TokenManagerEconomy(token.getString("display"));
                logChannel.warning("TokenManager is a beta feature. Report all bugs");
            } else {
                Hook.TokenManager.disable("disabled via config");
            }
        }

        if (this.economy == null && Hook.Vault.isEnabled()) {
            this.economy = new VaultEconomy();
        }

        // Schematics support via WorldEdit
        if (Hook.WorldEdit.isEnabled()) {
            logChannel.debug("Loading WorldEdit provider...");
            this.worldEditProvider = new WorldEditProvider(this, new File(getDataFolder(), "schematics"), getLogger());

            if (!this.worldEditProvider.isPlatformAvailable()) {
                Hook.WorldEdit.disable("unsupported version");
            } else {
                boolean legacy = this.versionControl.isLegacy();
                File schematicsFolder = this.worldEditProvider.getFolder();
                if (!this.worldEditProvider.getFolder().exists()) {
                    schematicsFolder.mkdirs();
                    new Config(this, "configuration.schematics." + this.versionControl.getMat().build(),
                            schematicsFolder, "cage_lava.schem" + (legacy ? "atic" : "")).copy(false);
                    new Config(this, "configuration.schematics.main", schematicsFolder,
                            "bedrock_problem.schematic").copy(false);
                    if (!legacy) {
                        new Config(this, "configuration.schematics.main", schematicsFolder,
                                "small_temple.schem").copy(false);
                    }
                }
            }
        }

        // WorldGuard possible crash fix
        if (!Hook.WorldGuard.isEnabled()) {
            this.worldGuardProvider = null;
        }

        Hook.print(getLogger());

        // Reload engine, customItemsFactory, GUI Manager and so on
        // The things really depend on LuckyBlock instances
        reloadSystem();

        // Initialize listeners and commands
        logChannel.info("Loading listeners...");
        if (Hook.SlimeFun.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(new SlimeFunListener(luckyBlockEngine), this);
        }
        Bukkit.getPluginManager().registerEvents(new CoreListener(luckyBlockEngine, updater,
                worldGuardProvider, worldsFilter, configHolder), this);
        Bukkit.getPluginManager().registerEvents(entityLoadListener, this);
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new CraftListener(luckyBlockEngine), this);
        Bukkit.getPluginManager().registerEvents(guiManager, this);

        if (getVersionType().isFree()) {
            logChannel.info("If you love my plugin, support me with 5 stars review" +
                    "(https://www.spigotmc.org/resources/92026/) or check out " +
                    "premium plugin version (https://www.spigotmc.org/resources/94872/)");
        }

        logChannel.info("Loading commands...");
        commandsManager = new CommandsManager(luckyBlockEngine, this);
        PluginCommand command = Bukkit.getPluginCommand("ntdluckyblock");
        if (command == null) {
            throw new UnsupportedOperationException("Command not found");
        }
        command.setExecutor(commandsManager);
//        command.setTabCompleter(commandsManager); TODO

        // Inject API to static provider
        logChannel.info("Injecting API...");

        // Timings finish, print time taken to start all managers
        ms = (System.currentTimeMillis() - ms);
        logChannel.info("§6Enabled in " + Misc.getColorLevel(ms, 1000, 3000) + ms + " ms§6...");


        // TODO remove all below (tests)

        luckyBlockEngine.register(luckyBlockEngine.loadFromConfig(
                luckyBlockEngine.newLuckyBlockHolder(new LuckyBlockKey("danirod12",
                        ColorData.YELLOW, Material.HONEY_BLOCK, false)),
                new Config(new File(getDataFolder(), "luckyblocks"), "danirod12.yml").load()));

        LuckyBlock lb = luckyBlockEngine.loadFromConfig(
                luckyBlockEngine.newLuckyBlockHolder(new LuckyBlockKey("slime",
                        ColorData.GREEN, Material.SLIME_BLOCK, false)),
                new Config(new File(getDataFolder(), "luckyblocks"), "danirod12.yml").load());
        ItemStack stack = new ItemStack(Material.HONEY_BLOCK);
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§6§lHoney LuckyBlock");
        stack.setItemMeta(meta);
        lb.setIcon(stack);
        lb.setItem(stack);
        luckyBlockEngine.register(lb);

        lb = luckyBlockEngine.loadFromConfig(
                luckyBlockEngine.newLuckyBlockHolder(new LuckyBlockKey("slab",
                        ColorData.WHITE, Material.END_STONE_BRICK_SLAB, false)),
                new Config(new File(getDataFolder(), "luckyblocks"), "danirod12.yml").load());
        stack = new ItemStack(Material.END_STONE_BRICK_SLAB);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§6§lStrange LuckyBlock");
        stack.setItemMeta(meta);
        lb.setIcon(new ItemStack(Material.SCAFFOLDING));
        lb.setItem(stack);
        luckyBlockEngine.register(lb);
    }

    @Override
    public void onDisable() {
        if (this.guiManager != null) {
            this.guiManager.close();
        }
    }

    @Override
    public void reloadConfig() {
        boolean startup = false;
        if (this.configHolder.getConfig() == null) {
            startup = true;
            this.configHolder.setConfig(new Config(this, getDataFolder(), "config").copyMissedFields());
        }
        boolean prevLightSource = this.configHolder.lightSource;
        this.configHolder.dirtyPickUp();

        if (this.versionControl.isLegacy() && !this.configHolder.lightSource) {
            this.logChannel.warning("Light source cannot be disabled on 1.8 - 1.12 " +
                    "due vanilla texture glitch (Texture is blacked out)");
            this.configHolder.lightSource = true;
        }
        if (prevLightSource != this.configHolder.lightSource) {
            if (startup) {
                Bukkit.getScheduler().runTaskLater(this, this.entityLoadListener::updateAllLoaded, 111L);
            } else {
                this.entityLoadListener.updateAllLoaded();
            }
        }

        // v2.4.2 Nov 19, 2021 and older warning
        if (this.configHolder.getConfig().isSet("place.verify-name")
                || this.configHolder.getConfig().getBoolean("place.convert-factory")) {
            logChannel.severe("Your config contains field related to old storage scheme (v2.4.2) " +
                    "from 2021 year! If you are using plugin as long and have not dropped your map " +
                    "and player data, you should install version 2.8.8 or older in order to avoid issues");
            logChannel.info("To remove this message remove config fields `place.*`");
        }

        ConfigurationSection section = this.configHolder.getConfig().getConfigurationSection("chances");
        assert section != null;
        for (String key : section.getKeys(false)) {
            DropChance chance = DropChance.parse(key);
            if (chance == null) {
                continue;
            }
            chance.setWeight(section.getInt(key));
        }

        MessagesManager.reload(this.configHolder.getConfig().getString("language"));

        if (logChannel.setDebug(this.configHolder.getConfig().getBoolean("debug"))) {
            this.logChannel.warning("Debug mode is enabled! Note that this mode is only for developer!");
        }

        if (this.configHolder.reduceAuthorInfo && getVersionType().isFree()) {
            this.configHolder.reduceAuthorInfo = false;
            logChannel.warning("Sorry, but you cant disable author info in free plugin version.");
            logChannel.warning("Check out premium plugin version - https://www.spigotmc.org/resources/94872");
        }

        Config worlds = new Config(this, "configuration.other", null, "worlds").copyMissedFields();
        worldsFilter.set(new StringMatcher<>(new WorldListDataHandler(worlds.getBoolean("break-no-drop"),
                worlds.getBoolean("place-admins")), worlds.getString("mode"), worlds.getStringList("list")));
    }

    @Override
    public void reloadSystem() {
        long ms = System.currentTimeMillis();
        logChannel.info("Loading system...");
        luckyBlockEngine.unregister(null);

        for (String typeName : this.configHolder.getConfig().getStringList("enabled")) {
            try {
                LuckyBlockType type = LuckyBlockType.parse(typeName);
                if (type == null) {
                    logChannel.warning("LuckyBlockType named " + typeName + " was not found");
                    continue;
                }
                luckyBlockEngine.load(type);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        logChannel.info("Loaded " + luckyBlockEngine.getLoadedTypes().length + " LuckyBlocks");

        try {
            CustomItemFactory.loadSystem(new HashMap<String, String>() {
                {
                    Arrays.stream(MessagesManager.Message.values())
                            .filter(msg -> msg.getPath().startsWith("custom_item."))
                            .forEach(msg -> put(msg.getPath().substring(12),
                                    msg.getAsString(true)));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // Refresh gui manager
        this.guiManager.reload();
        logChannel.info("System loaded (took " + (System.currentTimeMillis() - ms) + " ms)...");
    }

    private void checkForUpdates(boolean inform) {
        this.getSpigotUpdater().checkForUpdates(
                logChannel.isDebug() || !this.configHolder.webUnavailableDisable,
                inform || this.configHolder.informAboutUpdates
        );
    }

    @Override
    public ISpigotUpdater getSpigotUpdater() {
        return this.updater;
    }
}
