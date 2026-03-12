package com.github.danirod12.luckyblock.engine;

import com.github.danirod12.luckyblock.LBMain;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.customitem.CustomItemFactory;
import com.github.danirod12.luckyblock.api.event.LuckyDropEvent;
import com.github.danirod12.luckyblock.api.exception.EntryFormatException;
import com.github.danirod12.luckyblock.api.exception.PremiumVersionRequiredException;
import com.github.danirod12.luckyblock.api.loader.PathLoader;
import com.github.danirod12.luckyblock.api.loader.StringLoader;
import com.github.danirod12.luckyblock.api.model.*;
import com.github.danirod12.luckyblock.api.provider.GenerationFactoryProvider;
import com.github.danirod12.luckyblock.api.provider.LuckyEngineProvider;
import com.github.danirod12.luckyblock.api.provider.LuckyRecipeProvider;
import com.github.danirod12.luckyblock.api.setup.AnimationSetup;
import com.github.danirod12.luckyblock.api.setup.ShopSetup;
import com.github.danirod12.luckyblock.api.util.Config;
import com.github.danirod12.luckyblock.api.util.LogChannel;
import com.github.danirod12.luckyblock.api.util.Pair;
import com.github.danirod12.luckyblock.engine.drop.EntityDrop;
import com.github.danirod12.luckyblock.engine.drop.ItemDrop;
import com.github.danirod12.luckyblock.engine.loader.ConvertFactory;
import com.github.danirod12.luckyblock.engine.loader.JSONLoader;
import com.github.danirod12.luckyblock.engine.loader.LegacyLoader;
import com.github.danirod12.luckyblock.engine.manager.BaseDataGenerator;
import com.github.danirod12.luckyblock.engine.manager.GenerationFactory;
import com.github.danirod12.luckyblock.engine.model.LuckyBlockHolder;
import com.github.danirod12.luckyblock.engine.model.LuckyEntryHolder;
import com.github.danirod12.luckyblock.hook.sk89q.WorldEditProvider;
import com.github.danirod12.luckyblock.nms.VersionControlFactory;
import com.github.danirod12.luckyblock.nms.material.IMat;
import com.github.danirod12.luckyblock.recipe.LuckyRecipe;
import com.github.danirod12.luckyblock.recipe.LuckyRecipeProviderImpl;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.config.ConfigHolder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LuckyBlockEngine implements LuckyEngineProvider {

    private static final double ASSOCIATION_OFFSET = 1.2;

    @Getter
    private final Plugin plugin;
    @Getter
    private final LogChannel logChannel;
    private final File folder;
    @Getter
    private final ConfigHolder configHolder;
    private final VersionControlFactory versionControl;
    @Getter
    private final ConvertFactory convertFactory;

    private final StringLoader stringLoader;
    private final PathLoader pathLoader;
    private final LuckyRecipeProvider recipeProvider;
    private final GenerationFactoryProvider generationFactory;

    private final Map<LuckyBlockKey, LuckyBlock> map = new HashMap<>();
    private final Set<Material> materialsRegistry = new HashSet<>();

    public LuckyBlockEngine(Plugin plugin, LogChannel logChannel, File folder, ConfigHolder configHolder,
                            VersionControlFactory versionControl, WorldEditProvider worldEditProvider) {
        this.plugin = plugin;
        this.logChannel = logChannel;
        this.folder = folder;
        this.configHolder = configHolder;
        this.versionControl = versionControl;
        this.stringLoader = new LegacyLoader(this, worldEditProvider);
        this.pathLoader = new JSONLoader(this);
        this.recipeProvider = new LuckyRecipeProviderImpl(this);
        this.generationFactory = new GenerationFactory(this);
        this.convertFactory = new ConvertFactory();
    }

    @Override
    public VersionControlFactory getVersionControl() {
        return versionControl;
    }

    @Override
    public LuckyRecipeProvider getRecipeProvider() {
        return recipeProvider;
    }

    @Override
    public GenerationFactoryProvider getGenerationFactory() {
        return generationFactory;
    }

    @Override
    public boolean isLightSource() {
        return this.configHolder.lightSource;
    }

    @Override
    public StringLoader getStringLoader() {
        return stringLoader;
    }

    @Override
    public PathLoader getPathLoader() {
        return pathLoader;
    }

    @Override
    public void load(LuckyBlockType type) {
        if ((type == LuckyBlockType.ICED || type == LuckyBlockType.TINTED)
                && !LuckyBlockAPI.getVersionType().isPremium()) {
            logChannel.warning("LuckyBlock type " + type.name() + " cannot be loaded (Premium version is required)");
            return;
        }
        if (type == LuckyBlockType.TINTED && this.versionControl.getTintedMaterial() == null) {
            logChannel.warning("LuckyBlock type TINTED cannot be loaded (MC 1.17+ is required)");
            return;
        }

        LuckyBlockKey key = BaseDataGenerator.getKey(versionControl, type);
        Config config = getNewConfigInstance(type.name());
        if (!config.hasResource()) {
            config.createFile();
            config.load();
            generationFactory.generateBaseData(config, key);
            LuckyEntry[] entries = generationFactory.generateLuckyEntries(150, 200);
            generationFactory.saveLuckyEntries(config, entries);
            config.save();
        }
        config.copy(true);
        register(loadFromConfig(newLuckyBlockHolder(key), config));
    }

    @Override
    public Config getNewConfigInstance(String typeName) {
        return new Config(plugin, "configuration.luckyblocks." + versionControl.getMat().build(),
                folder, typeName.toLowerCase() + ".yml");
    }

    @Override
    public LuckyBlock loadFromConfig(LuckyBlock luckyBlock, Config config) {
        LuckyBlockKey type = luckyBlock.getKey();

        // Load main item from config and then apply it to holder
        String texture = this.getConfigField(config, "texture", String.class, () -> BaseDataGenerator.getTexture(type));
        String name = Misc.setColors(this.getConfigField(config, "name", String.class, type::getKey));
        List<String> lore = new ArrayList<>();
        if (config.isSet("lore")) {
            lore.addAll(config.getStringList("lore").stream().map(Misc::setColors).collect(Collectors.toList()));
        }
        luckyBlock.setItem(this.versionControl.getPlayerHead(texture, name, lore, this.genUUID(type)));

        // Load shop settings from config and then apply it to holder
        boolean shopEnabled = this.getConfigField(config, "shop", Boolean.class, () -> true);
        shopEnabled |= config.getBoolean("eco");
        int price = this.getConfigField(config, "price", Integer.class, () -> 250);
        luckyBlock.setShopSetup(new ShopSetup(shopEnabled, price));

        // Load animation settings from config and then apply if to holder
        boolean animationEnabled = this.getConfigField(config, "animation", Boolean.class, () -> true);
        String animation = this.getConfigField(config, "animation_type", String.class, () -> "MOBSPAWNER_FLAMES");
        Effect effect = Effect.MOBSPAWNER_FLAMES;
        try {
            effect = Effect.valueOf(animation.toUpperCase());
        } catch (Exception exception) {
            this.logChannel.warning("[" + config.getName() + "] Effect named `" + animation
                    + "` not found. https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Effect.html");
        }
        luckyBlock.setAnimationSetup(new AnimationSetup(animationEnabled, effect));

        // Load custom name from config and then apply it to holder
        luckyBlock.setCustomName(this.getConfigField(config, "custom_name", String.class, type::getDefaultCustomName));

        // Load default and custom crafts if possible
        if (this.getConfigField(config, "craft.default", Boolean.class, () -> true)) {
            luckyBlock.addRecipes(this.recipeProvider.getDefaultCrafts(type).toArray(new LuckyRecipe[0]));
        }
        if (this.getConfigField(config, "craft.custom", Boolean.class, () -> false)) {
            luckyBlock.addRecipes(this.recipeProvider.getCustomCrafts(type).toArray(new LuckyRecipe[0]));
        }

        // Add all configured LuckyEntries
        int skippedPremium = 0;
        int skippedEntries = 0;
        for (String str : config.getConfigurationSection("drop").getKeys(false)) {
            try {
                luckyBlock.getItemsBag().add(this.loadLuckyEntry(config, "drop." + str));
            } catch (PremiumVersionRequiredException exception) {
                skippedPremium++;
            } catch (EntryFormatException exception) {
                skippedEntries++;
            }
        }
        if (skippedEntries > 0 || skippedPremium > 0) {
            this.logChannel.warning("Skipped " + (skippedEntries + skippedPremium)
                    + " entries for " + config.getName() + " due misconfiguration"
                    + (skippedPremium > 0 ? " (" + skippedPremium + " premium items)" : ""));
        }

        // Check if we have made any updates. If so, write it to remote. Then remove completed object
        if (config.hasChanges()) {
            config.save();
        }
        return luckyBlock;
    }

    @Override
    public UUID genUUID(LuckyBlockKey type) {
        return BaseDataGenerator.genUUID(type);
    }

    @Override
    public LuckyEntry loadLuckyEntry(Config config, String path)
            throws EntryFormatException, PremiumVersionRequiredException {
        if (config == null || !config.isLoaded()) {
            throw new IllegalArgumentException("Config is not loaded");
        }

        LuckyEntry entry = null;
        // If path contains items field, we are dealing with modern JSON loader
        if (config.isSet(path + ".items")) {
            // I am so sorry, but we hate hackers. I do not know why someone run free after premium
            if (!LBMain.isPremium()) {
                throw new PremiumVersionRequiredException("JSON loader");
            }

            DropChance chance = DropChance.MEDIUM;
            try {
                chance = DropChance.valueOf(config.getString(path + ".chance").toUpperCase());
            } catch (Exception ignored) {
            }
            entry = new LuckyEntryHolder(chance);
            ConfigurationSection section = config.getConfigurationSection(path + ".items");
            for (String key : section.getKeys(false)) {
                try {
                    LuckyDrop drop = this.pathLoader.load(config.getConfigurationSection(path + ".items." + key));
                    if (drop != null) {
                        entry.add(drop);
                    }
                } catch (Throwable throwable) {
                    notifyMisconfiguration(config.getName(), path, "json", throwable);
                }
            }
        } else if (config.isSet(path)) {
            entry = new LuckyEntryHolder();
            List<String> list = config.getStringList(path);
            for (String dropData : list) {
                try {
                    LuckyDrop drop = this.stringLoader.deserialize(dropData);
                    if (drop != null) {
                        entry.add(drop);
                    }
                } catch (Throwable throwable) {
                    notifyMisconfiguration(config.getName(), path, "legacy", throwable);
                }
            }
            this.convertFactory.add(config, path);
        }
        if (entry != null && entry.isEmpty()) {
            this.logChannel.warning("Skipping entry with 0 items... " + config.getName() + ", " + path);
            entry = null;
        }
        if (entry == null) {
            throw new EntryFormatException(config.getName(), path);
        }
        return entry;
    }

    @Override
    public boolean isLuckyBlock(ItemStack stack) {
        return this.parseLuckyBlock(stack).isPresent();
    }

    @Override
    public boolean isLuckyBlock(Block block) {
        return this.searchByBlock(block) != null;
    }

    @Override
    public Optional<LuckyBlockKey> parseLuckyBlock(ItemStack stack) {
        String type = CustomItemFactory.parseValue(stack, CustomItemFactory.TAG_LUCKYBLOCK_TYPE);
        if (type == null) {
            return Optional.empty();
        }
        return Optional.of(get(type));
    }

    @Override
    public Optional<LuckyBlock> get(LuckyBlockKey type) {
        return Optional.ofNullable(this.map.get(type));
    }

    @Override
    public LuckyBlockKey get(String type) {
        return getLoaded(type).orElse(new LuckyBlockKey.NotLoadedLuckyBlockKey(type));
    }

    @Override
    public Optional<LuckyBlockKey> getLoaded(String key) {
        if (key.equalsIgnoreCase("random")) {
            return Optional.of(random());
        }
        for (LuckyBlockKey luckyBlockKey : this.map.keySet()) {
            if (luckyBlockKey.getKey().equals(key)) {
                return Optional.of(luckyBlockKey);
            }
        }
        return Optional.empty();
    }

    @Override
    public void register(LuckyBlock luckyBlock) {
        if (luckyBlock instanceof LuckyBlockHolder) {
            ((LuckyBlockHolder) luckyBlock).verifyDataOrThrowException();
            this.map.put(luckyBlock.getKey(), luckyBlock);
            this.materialsRegistry.add(luckyBlock.getKey().getMaterial());
            return;
        }
        throw new RuntimeException("Internal class mismatch. Use LuckyBlockEngine builder");
    }

    @Override
    public void unregister(LuckyBlockKey type) {
        if (type == null) {
            this.map.clear();
            this.materialsRegistry.clear();
        }
        LuckyBlock instance = this.map.remove(type);
        if (instance != null) {
            Material material = instance.getKey().getMaterial();
            if (this.map.keySet().stream().map(LuckyBlockKey::getMaterial).noneMatch(m -> m == material)) {
                this.materialsRegistry.remove(material);
            }
        }
    }

    @Override
    public LuckyBlock newLuckyBlockHolder(LuckyBlockKey type) {
        return new LuckyBlockHolder(this, type);
    }

    @Override
    public LuckyBlockKey[] getLoadedTypes() {
        return map.keySet().toArray(new LuckyBlockKey[0]);
    }

    @Override
    public LuckyBlock[] getLoaded() {
        return map.values().toArray(new LuckyBlock[0]);
    }

    private void notifyMisconfiguration(String name, String path, String loader, Throwable throwable) {
        String message = "An issue occurred while loading entry from (" + name + ", " + path + ") with "
                + loader + " loader. Misconfigured?";
        if (this.logChannel.isDebug()) {
            this.logChannel.warning(message, throwable);
        } else {
            message += " Set debug=true in config.yml to get more info";
            this.logChannel.warning(message);
        }
    }

    private <T> T getConfigField(Config config, String path, Class<T> clazz, Supplier<T> defaultValue) {
        if (clazz.isPrimitive()) {
            throw new RuntimeException("Primitive types not supported");
        }
        try {
            if (config.isSet(path)) {
                return clazz.cast(config.get().get(path));
            }
        } catch (ClassCastException ignored) {
        }
        T t = defaultValue.get();
        this.logChannel.info("[" + config.getName() + "] Path <" + path + "> not set or not a "
                + clazz.getSimpleName() + ". Setting default value...");
        config.set(path, t);
        return t;
    }

    @Override
    public Map<LuckyBlockKey, LuckyBlock> mapCopy() {
        return new HashMap<>(this.map);
    }

    @Override
    public LuckyBlockKey random() {
        LuckyBlockKey[] loaded = getLoadedTypes();
        if (loaded.length == 0) {
            return new LuckyBlockKey.NotLoadedLuckyBlockKey("noluckyblocks");
        }
        return loaded[ThreadLocalRandom.current().nextInt(loaded.length)];
    }

    @Override
    public File getFolder() {
        return this.folder;
    }

    @Override
    public String getLocatedName(LuckyBlockKey key, Location location) {
        return "ntdlb54;" + key.getKey() + ";" + (int) location.getX() + ";"
                + (int) location.getY() + ";" + (int) location.getZ();
    }

    @Override
    public Pair<LuckyBlockKey, ArmorStand> searchByBlock(Block block) {
        return block.getWorld().getNearbyEntities(block.getLocation().add(0.5, -1.2, 0.5),
                        0.1, 0.1, 0.1).stream().map(this::searchByEntity)
                .filter(Objects::nonNull).findAny().orElse(null);
    }

    @Override
    public Pair<LuckyBlockKey, ArmorStand> searchByEntity(Entity entity) {
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return null;
        }
        ArmorStand stand = (ArmorStand) entity;
        if (stand.getCustomName() == null) {
            return null;
        }

        String[] data = stand.getCustomName().split(";");
        if (data.length < 4) {
            return null;
        }

        if (data.length == 4) {
            // old version
            LuckyBlockType type = LuckyBlockType.parse(data[0]);
            if (type != null) {
                stand.setCustomName("ntdlb54;" + stand.getCustomName());
                String[] copy = new String[data.length + 1];
                System.arraycopy(data, 0, copy, 1, data.length);
                data = copy;
            } else {
                return null;
            }
        } else {
            // modern version
            if (!data[0].equals("ntdlb54")) {
                return null;
            }
        }

        // Location check
        Location location = stand.getLocation();
        if (!(data[2].equals(String.valueOf((int) location.getX()))
                && data[3].equals(String.valueOf((int) location.getY()))
                && data[4].equals(String.valueOf((int) location.getZ())))) {
            return null;
        }
        return new Pair<>(get(data[1]), stand);
    }

    @Override
    public Block getAssociatedBlock(Location location) {
        return location.clone().add(0, ASSOCIATION_OFFSET, 0).getBlock();
    }

    @Override
    public Location getAssociatedLocation(Block block) {
        return block.getLocation().add(0.5, -ASSOCIATION_OFFSET, 0.5);
    }

    @Override
    public List<Pair<Entity, LuckyBlockKey>> destroyEntities(
            boolean destroyAll, boolean destroyUnloaded, Entity... entities
    ) {
        List<Pair<Entity, LuckyBlockKey>> notRemoved = new ArrayList<>();
        for (Entity entity : entities) {
            Pair<LuckyBlockKey, ArmorStand> pair = searchByEntity(entity);
            if (pair == null) {
                notRemoved.add(new Pair<>(entity, null));
                continue;
            }
            Block block = this.getAssociatedBlock(entity.getLocation());
            if (!destroyAll) {
                // Maybe our LuckyBlock is not corrupted
                if (pair.getKey() instanceof LuckyBlockKey.NotLoadedLuckyBlockKey) {
                    // We do not know what type it should represent, remove all
                    if (!destroyUnloaded && block.getType().isSolid()) {
                        notRemoved.add(new Pair<>(entity, pair.getKey()));
                        continue;
                    }
                    //noinspection deprecation
                    if (block.getType() == pair.getKey().getMaterial() && (this.versionControl.isModern()
                            || block.getData() == pair.getKey().getColorData().getData())) {
                        notRemoved.add(new Pair<>(entity, pair.getKey()));
                        continue;
                    }
                }
            }
            if (block.getType().isSolid()) {
                block.setType(Material.AIR);
            }
            entity.remove();
        }
        return notRemoved;
    }

    @Override
    public void resolveSign(Block block) {
        if (versionControl.getMat().isSign(block.getType())) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            if (lines[0].equalsIgnoreCase("[ntdluckyblock]")) {
                LuckyBlockKey key = lines[1].equalsIgnoreCase("random") ? random() : get(lines[1]);
                if (key instanceof LuckyBlockKey.NotLoadedLuckyBlockKey) {
                    logChannel.warning("Cannot resolve sign. No loaded LuckyBlock key " + key.getKey());
                } else {
                    get(key).ifPresent(holder -> holder.placeBlock(block));
                }
            }
        }
    }

    @Override
    public List<Pair<LuckyBlockKey, ArmorStand>> searchByEntities(Collection<Entity> entities) {
        return entities.stream().map(this::searchByEntity).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void placeBlockForce(LuckyBlockKey key, Block block, ItemStack icon) {
        block.setType(Material.AIR);

        ArmorStand stand = (ArmorStand) block.getWorld().spawnEntity(
                getAssociatedLocation(block), EntityType.ARMOR_STAND);
        stand.setArms(false);
        stand.setCanPickupItems(false);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);
        stand.setCustomName(getLocatedName(key, stand.getLocation()));

        EntityEquipment entityEquipment = stand.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setHelmet(icon);

        // Light source feature
        // The trick is based on fire effect that is not rendering on client if stand is marker
        if (isLightSource()) {
            if (getVersionControl().isLegacy()) {
                // 1.8 - 1.12 ArmorStand (Or even Entity) meta apply is delayed
                // If we do not give 2 ticks delay, client will play fire animation
                Bukkit.getScheduler().runTaskLater(getLogChannel().getPlugin(),
                        () -> stand.setFireTicks(Integer.MAX_VALUE), 2L);
            } else {
                stand.setFireTicks(Integer.MAX_VALUE);
            }
        }

        // Update block. If we are on legacy MC version, apply block data
        block.setType(key.getMaterial());
        if (getVersionControl().isLegacy() && key.isApplyColorDataToBlock()) {
            IMat.setData(block, key.getColorData().getData());
        }
    }

    @Override
    public void placeBlock(LuckyBlockKey key, Block block) {
        LuckyBlockKey loaded = get(key.getKey());
        if (loaded instanceof LuckyBlockKey.NotLoadedLuckyBlockKey) {
            return;
        }
        placeBlockForce(loaded, block, get(key).map(LuckyBlock::getIcon).orElseThrow(RuntimeException::new));
    }

    @Override
    public void clearBlock(Block block, boolean setAir) {
        block.getWorld()
                .getNearbyEntities(
                        getAssociatedLocation(block),
                        0.1, 0.1, 0.1
                )
                .stream()
                .filter(entity -> searchByEntity(entity) != null)
                .forEach(Entity::remove);
        if (setAir) {
            block.setType(Material.AIR);
        }
    }

    @Override
    public List<Material> getMaterialsRegistry() {
        return new ArrayList<>(materialsRegistry);
    }

    @Override
    public boolean isLuckyBlock(Material material) {
        return materialsRegistry.contains(material);
    }

    @Override
    public boolean breakLuckyBlock(Plugin instance, Block block, Player player,
                                   boolean dropItems, boolean ignoreCancelled) {
        Pair<LuckyBlockKey, ArmorStand> pair = searchByBlock(block);
        if (pair != null) {
            LuckyBlock luckyBlock = get(pair.getKey()).orElse(null);
            if (luckyBlock == null || luckyBlock.playOpen(instance, block, player, dropItems, ignoreCancelled)) {
                block.setType(Material.AIR);
                pair.getValue().remove();
            }
        }
        return false;
    }

    @Override
    public boolean executeDrop(Plugin instance, LuckyDrop drop, LuckyBlockKey related, Block block, Player player) {
        LuckyDrop.Execution execution = new LuckyDrop.Execution(instance, related, block, player);
        LuckyDropEvent event = new LuckyDropEvent(execution, drop);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        try {
            drop.execute(execution);
        } catch (Throwable th) {
            if (th.getMessage() != null && !this.logChannel.isDebug()) {
                if (drop instanceof EntityDrop) {
                    return true;
                }
                if (drop instanceof ItemDrop && th.getMessage().toLowerCase().contains("air")) {
                    return true;
                }
            }
            th.printStackTrace();
            return false;
        }
        return true;
    }
}
