package com.github.danirod12.luckyblock.api.provider;

import com.github.danirod12.luckyblock.api.exception.EntryFormatException;
import com.github.danirod12.luckyblock.api.exception.PremiumVersionRequiredException;
import com.github.danirod12.luckyblock.api.loader.PathLoader;
import com.github.danirod12.luckyblock.api.loader.StringLoader;
import com.github.danirod12.luckyblock.api.model.*;
import com.github.danirod12.luckyblock.api.util.Config;
import com.github.danirod12.luckyblock.api.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public interface LuckyEngineProvider {

    StringLoader getStringLoader();

    PathLoader getPathLoader();

    void load(LuckyBlockType type);

    Config getNewConfigInstance(String typeName);

    LuckyBlock loadFromConfig(LuckyBlock luckyBlock, Config config);

    LuckyEntry loadLuckyEntry(Config config, String path)
            throws EntryFormatException, PremiumVersionRequiredException;

    boolean isLuckyBlock(ItemStack stack);

    boolean isLuckyBlock(Block block);

    Optional<LuckyBlockKey> parseLuckyBlock(ItemStack stack);

    Optional<LuckyBlock> get(LuckyBlockKey type);

    LuckyBlockKey get(String type);

    Optional<LuckyBlockKey> getLoaded(String key);

    void register(LuckyBlock luckyBlock);

    void unregister(LuckyBlockKey type);

    LuckyBlock newLuckyBlockHolder(LuckyBlockKey type);

    LuckyBlockKey[] getLoadedTypes();

    LuckyBlock[] getLoaded();

    Map<LuckyBlockKey, LuckyBlock> mapCopy();

    LuckyBlockKey random();

    File getFolder();

    VersionControl getVersionControl();

    LuckyRecipeProvider getRecipeProvider();

    GenerationFactoryProvider getGenerationFactory();

    boolean isLightSource();

    String getLocatedName(LuckyBlockKey key, Location location);

    Pair<LuckyBlockKey, ArmorStand> searchByBlock(Block block);

    Pair<LuckyBlockKey, ArmorStand> searchByEntity(Entity entity);

    Block getAssociatedBlock(Location location);

    Location getAssociatedLocation(Block block);

    List<Pair<Entity, LuckyBlockKey>> destroyEntities(
            boolean destroyAll, boolean destroyUnloaded, Entity... entities
    );

    void resolveSign(Block block);

    List<Pair<LuckyBlockKey, ArmorStand>> searchByEntities(Collection<Entity> entities);

    UUID genUUID(LuckyBlockKey type);

    void placeBlockForce(LuckyBlockKey key, Block block, ItemStack icon);

    void placeBlock(LuckyBlockKey key, Block block);

    void clearBlock(Block block, boolean setAir);

    List<Material> getMaterialsRegistry();

    boolean isLuckyBlock(Material material);

    boolean breakLuckyBlock(Plugin instance, Block block, Player player, boolean dropItems, boolean ignoreCancelled);

    boolean executeDrop(Plugin instance, LuckyDrop drop, LuckyBlockKey related, Block block, Player player);
}
