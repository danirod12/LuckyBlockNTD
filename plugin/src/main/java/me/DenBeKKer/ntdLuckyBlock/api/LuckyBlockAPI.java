package me.DenBeKKer.ntdLuckyBlock.api;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.api.loader.PathLoader;
import me.DenBeKKer.ntdLuckyBlock.api.loader.StringLoader;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.customitem.Identifier;
import me.DenBeKKer.ntdLuckyBlock.loader.JSONLoader;
import me.DenBeKKer.ntdLuckyBlock.loader.LegacyLoader;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

public class LuckyBlockAPI {

    private final static StringLoader LEGACY = new LegacyLoader();
    private final static PathLoader JSON = new JSONLoader();

    /**
     * @return LegacyLoader (Free version config loader)
     */
    public static StringLoader getLegacyLoader() {
        return LEGACY;
    }

    /**
     * @return JSON loader (Premium version config loader)
     */
    public static PathLoader getJSONLoader() {
        return JSON;
    }

    /**
     * @param loaded Config for LuckyEntry loading
     * @param path   Config path
     * @return LuckyEntry from config path
     */
    public static LuckyEntry loadDropEntry(Config loaded, String path) {

        if (loaded == null) throw new NullPointerException("Config is unloaded");

        if (loaded.get().isSet(path + ".items")) {

            if (!LBMain.isPremium()) {
                LBMain.log(Level.SEVERE, "Configuration path " + loaded.getName() + "/" + path
                        + " was converted to JSON format. Free version supports only legacy formats");
                return new LuckyEntry(DropChance.MEDIUM, new ItemDrop(new ItemStack(Material.STONE)));
            }

            String ch = loaded.get().getString(path + ".chance");
            DropChance chance = DropChance.MEDIUM;
            try {
                chance = DropChance.valueOf(ch);
            } catch (Exception ex) {
                LBMain.log(Level.WARNING, "Drop chance \""
                        + (ch == null ? "null" : ch) + "\" is unsupported (" + loaded.getName() + ", " + path + ".chance)");
            }
            LuckyEntry entry = new LuckyEntry(chance);
            for (String drop0 : loaded.get().getConfigurationSection(path + ".items").getKeys(false)) {

                try {
                    LuckyDrop drop = JSON.load(loaded, path + ".items." + drop0);
                    if (drop != null)
                        entry.add(drop);
                } catch (Throwable th) {

                    if (LBMain.isDebug())
                        th.printStackTrace();

                }

            }
            return entry;

        }

        if (loaded.get().isSet(path)) {

            List<String> list = loaded.get().getStringList(path);
            LuckyEntry entry = new LuckyEntry();
            for (String drop0 : list) {

                LuckyDrop drop = LEGACY.load(drop0);
                if (drop != null)
                    entry.add(drop);

            }
            LBMain.getInstance().getConvertManager().add(loaded, path);
            return entry;

        }

        return null;

    }

    /**
     * @deprecated Legacy: {@link #getLegacyLoader()}
     * JSON: {@link #getJSONLoader()}
     */
    @Deprecated
    public static LuckyDrop loadDrop(String drop0) {
        return LEGACY.load(drop0);
    }

    /**
     * @deprecated Get LuckyBlock using {@link LuckyBlockType#get()} and add intent {@link LuckyBlock#addIntent(LuckyEntry)}
     */
    @Deprecated
    public static void addLuckyDrop(LuckyBlockType type, LuckyDrop drop) throws LuckyBlockNotLoadedException {
        addLuckyDrop(type, Arrays.asList(drop));
    }

    /**
     * @deprecated Get LuckyBlock using {@link LuckyBlockType#get()} and add intent {@link LuckyBlock#addIntent(LuckyEntry)}
     */
    @Deprecated
    public static void addLuckyDrop(LuckyBlockType type, LuckyDrop... drop) throws LuckyBlockNotLoadedException {
        addLuckyDrop(type, Arrays.asList(drop));
    }

    /**
     * @deprecated Get LuckyBlock using {@link LuckyBlockType#get()} and add intent {@link LuckyBlock#addIntent(LuckyEntry)}
     */
    @Deprecated
    public static void addLuckyDrop(LuckyBlockType type, Collection<LuckyDrop> drop) throws LuckyBlockNotLoadedException {

        LuckyBlock block = type.get();
        block.addIntent(new LuckyEntry(drop));

    }

    @Deprecated
    public static boolean isLuckyBlock(ItemStack item) {
        return getLuckyBlock(item) != null;
    }

    @Deprecated
    public static LuckyBlockType getLuckyBlock(ItemStack item) {

        return getLuckyBlock(item, false, true);

    }

    @Deprecated
    public static boolean isLuckyBlock(ItemStack item, boolean unavailable, boolean checkUUID) {
        return getLuckyBlock(item, unavailable, checkUUID) != null;
    }

    @Deprecated
    public static LuckyBlockType getLuckyBlock(ItemStack item, boolean unavailable, boolean checkUUID) {
        return parseLuckyBlock(item, checkUUID, false);
    }

    /**
     * Check that item is LuckyBlock
     *
     * @param stack ItemStack to be checked
     * @return Checks that item is LuckyBlock
     */
    public static boolean checkLuckyBlock(ItemStack stack) {
        return checkLuckyBlock(stack, true);
    }

    /**
     * Check that item is LuckyBlock
     *
     * @param stack     ItemStack to be checked
     * @param check_tag Check tag
     * @return Checks that item is LuckyBlock
     */
    public static boolean checkLuckyBlock(ItemStack stack, boolean check_tag) {
        return checkLuckyBlock(stack, true, check_tag);
    }

    /**
     * Check that item is LuckyBlock
     *
     * @param stack      ItemStack to be checked
     * @param check_uuid Check uuid
     * @param check_tag  Check tag
     * @return Checks that item is LuckyBlock
     */
    public static boolean checkLuckyBlock(ItemStack stack, boolean check_uuid, boolean check_tag) {
        return parseLuckyBlock(stack, check_uuid, check_tag) != null;
    }

    /**
     * Parse LuckyBlock type from ItemStack
     *
     * @param stack ItemStack to be parsed
     * @return LuckyBlockType from item if item is LuckyBlock and null if not
     */
    public static LuckyBlockType parseLuckyBlock(ItemStack stack) {
        return parseLuckyBlock(stack, true);
    }

    /**
     * Parse LuckyBlock type from ItemStack
     *
     * @param stack     ItemStack to be parsed
     * @param check_tag Check tag
     * @return LuckyBlockType from item if item is LuckyBlock and null if not
     */
    public static LuckyBlockType parseLuckyBlock(ItemStack stack, boolean check_tag) {
        return parseLuckyBlock(stack, true, check_tag);
    }

    /**
     * Parse LuckyBlock type from ItemStack
     *
     * @param stack      ItemStack to be parsed
     * @param check_uuid Check uuid
     * @param check_tag  Check tag
     * @return LuckyBlockType from item if item is LuckyBlock and null if not
     */
    public static LuckyBlockType parseLuckyBlock(ItemStack stack, boolean check_uuid, boolean check_tag) {

        UUID uuid = null;
        if (check_uuid) {
            uuid = Misc.getUUID(stack);
            if (uuid == null) return null;
        }

        if (check_tag) {

            final String type = CustomItemFactory.parseValue(stack, CustomItemFactory.TAG_LUCKYBLOCK_TYPE);
            final LuckyBlockType parsed = LuckyBlockType.parse(type);
            return parsed == null || uuid == null || parsed.getUUID().equals(uuid) ? parsed : null;

        } else return LuckyBlockType.parse(uuid);

    }

    /**
     * @param stack    ItemStack to mark
     * @param tag_name Tag name
     * @param plugin   Plugin
     * @param value    Value to insert
     * @return Insert custom tag
     */
    public static ItemStack insertTag(ItemStack stack, String tag_name, Plugin plugin, String value) {
        return new Identifier(plugin, tag_name, value).apply(stack);
    }

    /**
     * @param stack    ItemStack to check
     * @param tag_name Tag name
     * @param plugin   Plugin
     * @param value    Value
     * @return Check if ItemStack has tag and value is same
     */
    public static boolean compareTag(ItemStack stack, String tag_name, Plugin plugin, String value) {
        return new Identifier(plugin, tag_name, value).compare(stack);
    }

    /**
     * @param b    Block that will be replaced
     * @param type LuckyBlockType to be inserted
     */
    public static void placeLuckyBlock(Block b, LuckyBlockType type) throws LuckyBlockNotLoadedException {
        type.get().placeBlock(b, false);
    }

    public static LuckyBlockType parseOldLuckyBlock(ItemStack stack) {

        UUID uuid = Misc.getUUID(stack);
        if (uuid == null) return null;
        if (CustomItemFactory.parseValue(stack, CustomItemFactory.TAG_LUCKYBLOCK_TYPE) != null) return null;

        LuckyBlockType type = LuckyBlockType.parse(uuid);
        if (type == null) {

            // 1.8 display name could be null
            if (stack.getItemMeta() == null || stack.getItemMeta().getDisplayName() == null) return null;
            for (Map.Entry<LuckyBlockType, LuckyBlock> loaded : LuckyBlockType.map().entrySet())
                if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(loaded.getValue().getOldName()))
                    return loaded.getKey();

        }
        return type;

    }

    /**
     * @param b Block to be checked
     * @return Check if block is LuckyBlock
     */
    public static boolean isLuckyBlock(Block b) {

        if (b.getType().name().toUpperCase().contains("STAINED_GLASS") ||
                b.getType().name().equalsIgnoreCase("TINTED_GLASS") || b.getType() == Material.ICE) {

            for (LuckyBlockType type : LuckyBlockType.values()) {

                //if(e.getBlock().getType() == type.getMaterial()) {

                for (Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {

                    if (en.getType() != EntityType.ARMOR_STAND) continue;
                    ArmorStand stand = (ArmorStand) en;
                    if (stand.getCustomName() == null) continue;
                    if (stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int) stand.getLocation().getX()
                            + ";" + (int) stand.getLocation().getY() + ";" + (int) stand.getLocation().getZ())
                            && stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {

                        return true;

                    }

                }

                //}

            }

        }
        return false;

    }

    @Deprecated
    public static void resolve_sign(Block block, boolean update) {
        resolveSign(block, update);
    }

    public static void resolveSign(Block block, boolean update) {

        if (LBMain.getInstance().factory.isOakSign(block.getType())) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            if (lines[0].equalsIgnoreCase("[ntdluckyblock]")) {

                LuckyBlockType type = lines[1].equalsIgnoreCase("random") ? LuckyBlockType.random(true) : LuckyBlockType.parse(lines[1]);

                if (type == null) {
                    LBMain.log(Level.WARNING, "LuckyBlock type " + lines[1].toUpperCase() + " not found");
                } else {

                    try {
                        LuckyBlockAPI.placeLuckyBlock(block, type);
                    } catch (LuckyBlockNotLoadedException e) {
                        LBMain.log(Level.WARNING, "LuckyBlock type " + type.name() + " was not loaded");
                    }

                }

            }
        } else if (update && block.isLiquid()) {
            Material item = block.getType();
            block.setType(Material.AIR);
            block.setType(item);
        }

    }

    /**
     * @deprecated {@link #breakLuckyBlock0(Block, boolean)}
     */
    @Deprecated
    public static void breakLuckyBlock0(Block b) {
        breakLuckyBlock0(b, false);
    }

    /**
     * If you want destroy luckyblock without drop use {@link #breakLuckyBlock(Block, Player, boolean, boolean)} (Player null)
     *
     * @param b      Block to break
     * @param ignore Ignore cancellable
     */
    public static void breakLuckyBlock0(Block b, boolean ignore) {

        if (b.getType().name().toUpperCase().contains("STAINED_GLASS") ||
                b.getType().name().equalsIgnoreCase("TINTED_GLASS") || b.getType() == Material.ICE) {

            for (LuckyBlockType type : LuckyBlockType.values()) {

                for (Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {

                    if (en.getType() != EntityType.ARMOR_STAND) continue;
                    ArmorStand stand = (ArmorStand) en;
                    if (stand.getCustomName() == null) continue;
                    if (stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int) stand.getLocation().getX()
                            + ";" + (int) stand.getLocation().getY() + ";" + (int) stand.getLocation().getZ())
                            && stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {

                        if (type.isLoaded()) {
                            if (LuckyBlockType.map().get(type).tryOpen(b, ignore)) {
                                stand.remove();
                                b.setType(Material.AIR);
                            }
                        } else {
                            stand.remove();
                            b.setType(Material.AIR);
                        }

                        return;

                    }

                }

            }

        }

    }

    /**
     * @deprecated {@link #breakLuckyBlock(Block, Player, boolean, boolean)}
     */
    @Deprecated
    public static void breakLuckyBlock(Block b, Player p) {
        breakLuckyBlock(b, p, true);
    }

    /**
     * @deprecated {@link #breakLuckyBlock(Block, Player, boolean, boolean)}
     */
    @Deprecated
    public static void breakLuckyBlock(Block b, Player p, boolean drop) {
        breakLuckyBlock(b, p, drop, false);
    }

    /**
     * @param b      Block to break
     * @param p      Player that break it ( target )
     * @param drop   Drop items (IF DROP FALSE PLAYER MAY BE NULL)
     * @param ignore Ignore cancellable
     */
    public static void breakLuckyBlock(Block b, Player p, boolean drop, boolean ignore) {

        if (b.getType().name().toUpperCase().contains("STAINED_GLASS") ||
                b.getType().name().equalsIgnoreCase("TINTED_GLASS") || b.getType() == Material.ICE) {

            for (LuckyBlockType type : LuckyBlockType.values()) {

                for (Entity en : b.getWorld().getNearbyEntities(b.getLocation().add(0.5, -1.2, 0.5), 0.1, 0.1, 0.1)) {

                    if (en.getType() != EntityType.ARMOR_STAND) continue;
                    ArmorStand stand = (ArmorStand) en;
                    if (stand.getCustomName() == null) continue;
                    if (stand.getCustomName().equalsIgnoreCase(type.name() + ";" + (int) stand.getLocation().getX()
                            + ";" + (int) stand.getLocation().getY() + ";" + (int) stand.getLocation().getZ())
                            && stand.getLocation().add(0, 1.2, 0).getBlock().equals(b)) {

                        if (drop) {
                            if (type.isLoaded()) {
                                if (p == null) {
                                    throw new NullPointerException(
                                            "You must provide player for breakLuckyBlock(Block, Player, boolean) or use breakLuckyBlock0(Block)");
                                }
                                if (LuckyBlockType.map().get(type).tryOpen(b, p, ignore)) {
                                    stand.remove();
                                    b.setType(Material.AIR);
                                }
                            } else {
                                stand.remove();
                                b.setType(Material.AIR);
                            }
                        } else {
                            stand.remove();
                            b.setType(Material.AIR);
                        }

                        return;

                    }

                }

            }

        }

    }

    public static int destroyEntity(Entity[] array, boolean destroyAll) {

        int removed = 0;
        for (Entity entity : array) {

            if (entity.getType() != EntityType.ARMOR_STAND) continue;
            ArmorStand stand = (ArmorStand) entity;
            if (stand.getCustomName() == null || !stand.getCustomName().contains(";")) continue;

            final LuckyBlockType type = LuckyBlockType.parse(stand.getCustomName().split(";")[0]);
            if (type == null) continue;

            final Block block = stand.getWorld().getBlockAt(stand.getLocation().add(-.5, 1.2, -.5));
            if (type.getMaterial() == block.getType() && (!type.isColoredGlass()
                    || LBMain.getInstance().factory instanceof Mat1_13
                    || block.getData() == type.asColor().getData())) {

                if (!destroyAll) continue;
                block.setType(Material.AIR);

            }
            entity.remove();
            removed++;

        }
        return removed;

    }

    /**
     * Change {@link LuckyBlockType#map()} element to null
     *
     * @param type LuckyBlockType
     */
    public static void destroy(LuckyBlockType type) {
        LuckyBlockType.map().put(type, null);
    }

    /**
     * Change {@link LuckyBlockType#map()} element to null
     *
     * @param luckyblock LuckyBlock
     */
    public static void destroy(LuckyBlock luckyblock) {
        LuckyBlockType.map().put(luckyblock.getType(), null);
    }

    /**
     * Change {@link LuckyBlockType#map()} element to new one
     *
     * @param luckyblock LuckyBlock
     */
    public static void rewrite(LuckyBlock luckyblock) {
        LuckyBlockType.map().put(luckyblock.getType(), luckyblock);
    }

    public static void reloadSystem() {
        LBMain.getInstance().reloadSystem();
    }

    @Deprecated
    public static void system_load(Plugin deprecated) {
        reloadSystem();
    }

    public static String getVersion() {
        return LBMain.getInstance().getVersion();
    }

    public static String getLastUpdate() {
        return LBMain.getLastUpdate();
    }

    public static int getBuild() {
        return LBMain.getBuild();
    }

    public static LBMain getMainInstance() {
        return LBMain.getInstance();
    }

    public static boolean isPremium() {
        return LBMain.isPremium();
    }

}
