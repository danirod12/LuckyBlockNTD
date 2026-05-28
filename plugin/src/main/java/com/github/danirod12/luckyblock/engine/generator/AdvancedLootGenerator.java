package com.github.danirod12.luckyblock.engine.generator;

import com.github.danirod12.luckyblock.LBMain;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.SpecialDropType;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.engine.drop.EntityDrop;
import com.github.danirod12.luckyblock.engine.drop.ItemDrop;
import com.github.danirod12.luckyblock.api.model.random.Amount;
import com.github.danirod12.luckyblock.engine.drop.SchematicDrop;
import com.github.danirod12.luckyblock.engine.drop.SchematicList;
import com.github.danirod12.luckyblock.engine.drop.special.*;
import com.github.danirod12.luckyblock.util.random.WeightListAmount;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.cryptomorin.xseries.XMaterial;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class AdvancedLootGenerator {
    private final LuckyBlockEngine engine;
    private final AdvancedLootDatabase db;

    private final int minItems;
    private final int maxItems;
    private final SynergyMode mode; //TODO(zhabka_zhaba): Implement proper  mode functionality
    private final Material forcedCore;
    private final boolean generateSchematics;
    private static final List<EntityType> SAFE_ENTITIES;

    static {
        Set<EntityType> blacklist = EnumSet.of(
                EntityType.ENDER_DRAGON, EntityType.WITHER
        ); // TODO add json blacklist?

        SAFE_ENTITIES = Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .filter(type -> !blacklist.contains(type))
                .collect(Collectors.toList());
    }

    private AdvancedLootGenerator(Builder builder) {
        this.engine = builder.engine;
        this.db = builder.db;
        this.minItems = builder.minItems;
        this.maxItems = builder.maxItems;
        this.mode = builder.mode;
        this.forcedCore = builder.forcedCore;
        this.generateSchematics = builder.generateSchematics;
    }

    public static Builder builder(LuckyBlockEngine engine, AdvancedLootDatabase db) {
        return new Builder(engine, db);
    }

    public WeightListAmount<LuckyDrop> generate() {
        WeightListAmount<LuckyDrop> entry = generateLuckyEntry();
        int attempts = 0;

        while (entry.isEmpty() && attempts < 15) {
            entry = generateLuckyEntry();
            attempts++;
        }

        if (entry.isEmpty()) {
            entry.add(new ItemDrop(new ItemStack(Material.STONE)), 100.0, null);
            entry.setAmount(Amount.of("1"));
        }

        return entry;
    }

    private WeightListAmount<LuckyDrop> generateLuckyEntry() {
        WeightListAmount<LuckyDrop> entry = new WeightListAmount<>();

        XMaterial coreItemX = (forcedCore != null) ? XMaterial.matchXMaterial(forcedCore) : db.getRandomCore();

        ItemStack coreStack = coreItemX.parseItem();
        if (coreStack == null) {
            org.bukkit.Bukkit.getLogger().info("[DEBUG] XMaterial failed to parse: " + coreItemX.name());
            return entry;
        }

        coreStack.setAmount(calculateAmount(coreStack));

        try {
            de.tr7zw.nbtapi.NBT.itemStackToNBT(coreStack);
        } catch (Exception e) {
            org.bukkit.Bukkit.getLogger().info("[DEBUG] Core item is unserializable on this version: "
                    + coreItemX.name());
            return entry;
        }

        entry.add(new ItemDrop(coreStack), 100.0, null);

        CoreItem coreData = db.get(coreItemX);
        if (coreData == null) {
            return entry;
        }

        if (coreData.getSynergies() == null || coreData.getSynergies().isEmpty()) {
            return entry;
        }

        int limit = ThreadLocalRandom.current().nextInt(minItems, maxItems + 1);

        for (SynergyItem synItem : coreData.getSynergies()) {
            ItemStack synStack = synItem.getMaterial().parseItem();
            if (synStack == null) {
                continue;
            }

            synStack.setAmount(calculateAmount(synStack));

            try {
                de.tr7zw.nbtapi.NBT.itemStackToNBT(synStack);
            } catch (Exception e) {
                org.bukkit.Bukkit.getLogger().info("[DEBUG] Synergy skipped (unserializable): "
                        + synItem.getMaterial().name());
                continue;
            }

            double chance = (double) synItem.getSynweight();
            entry.add(new ItemDrop(synStack), chance, null);
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.1) { // TODO шансы в json??
            entry.add(generateEntity(), 30.0, null);
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            LuckyDrop special = generateSpecial();
            if (special != null) {
                entry.add(special, 25.0, null);
            }
        }

        if (generateSchematics && ThreadLocalRandom.current().nextDouble() < 0.1) {
            SchematicList.SchematicData sData = SchematicList.getRandomSchematic();

            if (sData != null) {
                Plugin plugin = JavaPlugin.getPlugin(LBMain.class);
                File schemFolder = new File(plugin.getDataFolder(), "schematics");
                File schemFile = new File(schemFolder, sData.fileName);

                if (schemFile.exists()) {
                    entry.add(new SchematicDrop(schemFile, sData.type, sData.ignoreAir), 10.0, null);
                }
            }
        }

        entry.setAmount(Amount.of(String.valueOf(limit)));

        return entry;
    }

    public static LuckyDrop generateEntity() {
        int amount = ThreadLocalRandom.current().nextInt(1, 4);
        EntityType type = SAFE_ENTITIES.get(ThreadLocalRandom.current().nextInt(SAFE_ENTITIES.size()));
        return new EntityDrop(type, amount);
    }

    public static LuckyDrop generateSpecial() {
        switch (ThreadLocalRandom.current().nextInt(18)) {
            case 0: return new WaterBucketSpecial(SpecialDropType.WATER_BUCKET.defaultValue());
            case 1: return new TntExplosionSpecial(SpecialDropType.TNT_EXPLOSION.defaultValue());
            case 2: return new TntColumnSpecial(SpecialDropType.TNT_COLUMN.defaultValue());
            case 3:
                int amount = ThreadLocalRandom.current().nextBoolean() ? 0 : ThreadLocalRandom.current().nextInt(20);
                return new PigSpecial(SpecialDropType.PIG.defaultValue() + amount);
            case 4: return new LightningSpecial(SpecialDropType.LIGHTNING.defaultValue());
            case 5: return new ExperienceExplosionSpecial(SpecialDropType.EXPERIENCE_EXPLOSION.defaultValue());
            case 6: return new JebSpecial(ThreadLocalRandom.current().nextInt(3, 8));
            case 7: return new CreepyMusicSpecial();
            case 8: return new ChickenRainSpecial(ThreadLocalRandom.current().nextInt(10, 20));
            case 9: return new ParanoiaSpecial();
            case 10: return new AnnoyingBabySpecial(ThreadLocalRandom.current().nextInt(2, 6));
            case 11: return new HotbarSwapSpecial();
            case 12: return new BlackHoleSpecial();
            case 13: return new GhostModeSpecial();
            case 14: return new MoonGravitySpecial();
            case 15: return new RandomTeleportSpecial();
            case 16: return new SlipperyFingersSpecial();
            case 17: return new TimeLoopSpecial();
            default: return null;
        }
    }


    private int calculateAmount(ItemStack stack) {
        Material material = stack.getType();

        if (!isItemSafe(material)) {
            return 1;
        }

        int maxStack = material.getMaxStackSize();
        if (maxStack == 1) {
            return 1;
        }

        //TODO(zhabka_zhaba): Implement rarity dependence
        if (material.isBlock()) {
            int[] sizes = {8, 16, 32}; //TODO(zhabka_zhaba): Implement non-linear size distribution?
            int amount = sizes[ThreadLocalRandom.current().nextInt(sizes.length)];
            return Math.min(amount, maxStack);
        }

        return ThreadLocalRandom.current().nextInt(1, Math.max(2, maxStack / 3));
    }

    private static boolean isItemSafe(Material material) {
        try {
            return material.isItem();
        } catch (NoSuchMethodError e) {
            return !material.name().contains("AIR");
        }
    }

    public static class Builder {
        private final LuckyBlockEngine engine;
        private final AdvancedLootDatabase db;
        private int minItems = 2;
        private int maxItems = 5;
        private SynergyMode mode = SynergyMode.STRICT;
        private Material forcedCore = null;
        private boolean generateSchematics = false;

        public Builder(LuckyBlockEngine engine, AdvancedLootDatabase db) {
            this.engine = engine;
            this.db = db;
        }

        public Builder minItems(int min) {
            this.minItems = min;
            return this;
        }

        public Builder maxItems(int max) {
            this.maxItems = max;
            return this;
        }

        public Builder mode(SynergyMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder forceCoreItem(Material material) {
            this.forcedCore = material;
            return this;
        }

        public Builder enableSchematics(boolean enable) {
            this.generateSchematics = enable;
            return this;
        }

        public AdvancedLootGenerator build() {
            return new AdvancedLootGenerator(this);
        }
    }
}
