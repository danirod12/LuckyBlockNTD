package com.github.danirod12.luckyblock.engine.generator;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.engine.drop.ItemDrop;
import com.github.danirod12.luckyblock.api.model.random.Amount;
import com.github.danirod12.luckyblock.util.random.WeightListAmount;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class AdvancedLootGenerator {
    private final LuckyBlockEngine engine;
    private final AdvancedLootDatabase db;

    private final int minItems;
    private final int maxItems;
    private final SynergyMode mode; //TODO(zhabka_zhaba): Implement proper  mode functionality
    private final Material forcedCore;

    private AdvancedLootGenerator(Builder builder) {
        this.engine = builder.engine;
        this.db = builder.db;
        this.minItems = builder.minItems;
        this.maxItems = builder.maxItems;
        this.mode = builder.mode;
        this.forcedCore = builder.forcedCore;
    }

    public static Builder builder(LuckyBlockEngine engine, AdvancedLootDatabase db) {
        return new Builder(engine, db);
    }

    public WeightListAmount<LuckyDrop> generate() {
        return generateLuckyEntry();
    }

    private WeightListAmount<LuckyDrop> generateLuckyEntry() {
        WeightListAmount<LuckyDrop> entry = new WeightListAmount<>();

        Material coreItem = (forcedCore != null) ? forcedCore : db.getRandomCore();

        org.bukkit.Bukkit.getLogger().info("[DEBUG] Starting new drop");
        org.bukkit.Bukkit.getLogger().info("[DEBUG] Core item: " + coreItem.name());

        entry.add(new ItemDrop(new ItemStack(coreItem, calculateAmount(coreItem))), 100.0, null);

        CoreItem coreData = db.get(coreItem);
        if (coreData == null) {
            org.bukkit.Bukkit.getLogger().info("[DEBUG] Error - no core item");
            return entry;
        }

        if (coreData.getSynergies() == null || coreData.getSynergies().isEmpty()) {
            org.bukkit.Bukkit.getLogger().info("[DEBUG] Item " + coreItem.name() + " has no synergies");
            return entry;
        }

        int limit = ThreadLocalRandom.current().nextInt(minItems, maxItems + 1);
        org.bukkit.Bukkit.getLogger().info("[DEBUG] Bundle mode: " + limit);

        for (SynergyItem synItem : coreData.getSynergies()) {
            int amount = calculateAmount(synItem.getMaterial());
            double chance = (double) synItem.getSynweight();

            entry.add(new ItemDrop(new ItemStack(synItem.getMaterial(), amount)), chance, null);

            org.bukkit.Bukkit.getLogger().info("[DEBUG] Synergy registered: "
                    + synItem.getMaterial().name() + " | Chance: " + chance + "%");
        }

        entry.setAmount(Amount.of(String.valueOf(limit)));

        return entry;
    }

    private int calculateAmount(Material material) {
        if (!material.isItem()) {
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

    public static class Builder {
        private final LuckyBlockEngine engine;
        private final AdvancedLootDatabase db;
        private int minItems = 2;
        private int maxItems = 5;
        private SynergyMode mode = SynergyMode.STRICT;
        private Material forcedCore = null;

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

        public AdvancedLootGenerator build() {
            return new AdvancedLootGenerator(this);
        }
    }
}
