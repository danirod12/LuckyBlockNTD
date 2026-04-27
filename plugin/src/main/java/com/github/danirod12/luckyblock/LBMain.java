package com.github.danirod12.luckyblock;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.customitem.CustomItemFactory;
import com.github.danirod12.luckyblock.api.model.*;
import com.github.danirod12.luckyblock.api.provider.LBMainProvider;
import com.github.danirod12.luckyblock.api.util.*;
import com.github.danirod12.luckyblock.command.CommandsManager;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.engine.drop.LuckyItemDrop;
import com.github.danirod12.luckyblock.engine.generator.AdvancedLootDatabase;
import com.github.danirod12.luckyblock.hook.Hook;
import com.github.danirod12.luckyblock.hook.economy.EconomyBridge;
import com.github.danirod12.luckyblock.hook.economy.TokenManagerEconomy;
import com.github.danirod12.luckyblock.hook.economy.VaultEconomy;
import com.github.danirod12.luckyblock.hook.sk89q.WorldEditProvider;
import com.github.danirod12.luckyblock.hook.sk89q.WorldGuardProvider;
import com.github.danirod12.luckyblock.hook.sk89q.WorldGuardProviderImpl;
import com.github.danirod12.luckyblock.hook.thebusybiscuit.SlimeFunListener;
import com.github.danirod12.luckyblock.listener.CoreListener;
import com.github.danirod12.luckyblock.listener.CustomItemListener;
import com.github.danirod12.luckyblock.listener.EntityLoadListener;
import com.github.danirod12.luckyblock.nms.VersionControlFactory;
import com.github.danirod12.luckyblock.recipe.CraftListener;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.SpigotUpdater;
import com.github.danirod12.luckyblock.util.StringMatcher;
import com.github.danirod12.luckyblock.util.Templates;
import com.github.danirod12.luckyblock.util.config.ConfigHolder;
import com.github.danirod12.luckyblock.util.manager.GuiManager;
import com.github.danirod12.luckyblock.util.manager.MessagesManager;
import com.github.danirod12.luckyblock.variables.PlayerHead;
import com.github.danirod12.luckyblock.variables.world.WorldListDataHandler;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    private AdvancedLootDatabase advancedLootDatabase;

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
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("§fStarting LuckyBlock (ntdLuckyBlock) v" + getVersion()
                + " [" + Templates.GIT_BRANCH + "-" + Templates.GIT_COMMIT_ID_ABBREV
                + "] (" + getLastUpdate() + "), " + getVersionType().getSimpleName());
        sender.sendMessage("§e  ╭╮╱╱╭╮╱╭┳━━━┳╮╭━┳╮╱╱╭┳━━╮╭╮╱╱╭━━━┳━━━┳╮╭━╮");
        sender.sendMessage("§e  ┃┃╱╱┃┃╱┃┃╭━╮┃┃┃╭┫╰╮╭╯┃╭╮┃┃┃╱╱┃╭━╮┃╭━╮┃┃┃╭╯");
        sender.sendMessage("§e  ┃┃╱╱┃┃╱┃┃┃╱╰┫╰╯╯╰╮╰╯╭┫╰╯╰┫┃╱╱┃┃╱┃┃┃╱╰┫╰╯╯");
        sender.sendMessage("§e  ┃┃╱╭┫┃╱┃┃┃╱╭┫╭╮┃╱╰╮╭╯┃╭━╮┃┃╱╭┫┃╱┃┃┃╱╭┫╭╮┃");
        sender.sendMessage("§e  ┃╰━╯┃╰━╯┃╰━╯┃┃┃╰╮╱┃┃╱┃╰━╯┃╰━╯┃╰━╯┃╰━╯┃┃┃╰╮");
        sender.sendMessage("§e  ╰━━━┻━━━┻━━━┻╯╰━╯╱╰╯╱╰━━━┻━━━┻━━━┻━━━┻╯╰━╯ NTD");
        sender.sendMessage("§f          Made with " + "§clove " + "§fby §a" + "danirod12");
        sender.sendMessage("");
        for (String entry : Templates.SUPPORT_INFO) {
            sender.sendMessage(entry);
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
                configHolder, versionControl, worldEditProvider == null ? null : worldEditProvider.getFolder());
        entityLoadListener = new EntityLoadListener(this, luckyBlockEngine);
        LuckyBlockAPI.injectAPI(this, luckyBlockEngine);

        // Loading config
        reloadConfig();

        // ============================================
        logChannel.info("Loading Advanced Loot Database...");
        advancedLootDatabase = new AdvancedLootDatabase();
        try {
            InputStream in = getResource("materials_tagged_v2.json");
            if (in == null) {
                logChannel.severe("Could not find materials_tagged_v2.json in plugin resources");
            } else {
                try (Reader reader = new InputStreamReader(in,
                        java.nio.charset.StandardCharsets.UTF_8)) {
                    advancedLootDatabase.load(reader);
                    logChannel.info("Advanced Loot Database loaded successfully.");
                }
            }
        } catch (Exception e) {
            logChannel.severe("Failed to load Advanced Loot Database");
        }
        // ============================================

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

        LuckyBlock luckyBlock;
        ItemStack stack;
        ItemMeta meta;

        // danirod12 skin LuckyBlock

        luckyBlock = luckyBlockEngine.newLuckyBlock(new LuckyBlockKey("danirod12",
                ColorData.YELLOW, Material.HONEY_BLOCK, false));
        luckyBlock.setItemAndIcon(
                this.versionControl.getPlayerHead(
                        "62e964f46cc23947d6db48334e42cfab7093184e4ae7d27f68c8e7079b3d21dc",
                        "danirod12", null, null
                )
        );
        luckyBlock.setCustomName("§6DANIROD12 SKIN");
        // TEST!!! (taking danirod12 block for testing)
        for (int i = 0; i < 30; i++) {
            com.github.danirod12.luckyblock.api.model.ItemsBag generatedBag =
                    com.github.danirod12.luckyblock.engine.generator.AdvancedLootGenerator.builder(luckyBlockEngine,
                                    advancedLootDatabase)
                            .minItems(2)
                            .maxItems(4)
                            .mode(com.github.danirod12.luckyblock.engine.generator.SynergyMode.STRICT)
                            .build()
                            .generate();

            for (com.github.danirod12.luckyblock.api.model.random
                    .LuckyCollection<com.github.danirod12.luckyblock.api.model.LuckyDrop>
                    entry : generatedBag.getAll()) {
                luckyBlock.getItemsBag().add(entry);
            }
        }
        // ------------------------------------

        luckyBlockEngine.register(luckyBlock);

        // honey LuckyBlock

        luckyBlock = luckyBlockEngine.newLuckyBlock(new LuckyBlockKey("slime",
                ColorData.GREEN, Material.SLIME_BLOCK, false));
        stack = new ItemStack(Material.HONEY_BLOCK);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§6§lHoney LuckyBlock");
        stack.setItemMeta(meta);
        luckyBlock.setItemAndIcon(stack);
        luckyBlock.getItemsBag().add(luckyBlockEngine.newLuckyEntry(new LuckyItemDrop(luckyBlock.getKey(), 1)));
        luckyBlockEngine.register(luckyBlock);

        // ender LuckyBlock with slab and scaffolding

        luckyBlock = luckyBlockEngine.newLuckyBlock(new LuckyBlockKey("slab",
                ColorData.WHITE, Material.END_STONE_BRICK_SLAB, false));
        stack = new ItemStack(Material.END_STONE_BRICK_SLAB);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§6§lStrange LuckyBlock");
        stack.setItemMeta(meta);
        luckyBlock.setIcon(new ItemStack(Material.SCAFFOLDING));
        luckyBlock.setItem(stack);
        luckyBlock.getItemsBag().add(luckyBlockEngine.newLuckyEntry(new LuckyItemDrop(luckyBlock.getKey(), 1)));
        luckyBlockEngine.register(luckyBlock);
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
//        assert section != null; TODO rework, pass to configuration loader
//        for (String key : section.getKeys(false)) {
//            com.github.danirod12.luckyblock.api.model.random.DropChance chance = DropChance.parse(key);
//            if (chance == null) {
//                continue;
//            }
//            chance.setWeight(section.getInt(key));
//        }

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
