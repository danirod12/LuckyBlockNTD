package me.DenBeKKer.ntdLuckyBlock.engine.manager;

import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.api.provider.GenerationFactoryProvider;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.EntityDrop;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.ItemDrop;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.LuckyItemDrop;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.RandomLuckyItemDrop;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.special.*;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Templates;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GenerationFactory implements GenerationFactoryProvider {

    private final LuckyBlockEngine engine;

    public GenerationFactory(LuckyBlockEngine engine) {
        this.engine = engine;
    }

    @Override
    public void generateBaseData(Config config, LuckyBlockKey type) {
        engine.getLogChannel().info("§fGenerating §" + type.getColorData().asColorCode() + type.getKey()
                + " LuckyBlock§f configuration §6(LBF version: 3)");
        FileConfiguration file = config.get();
        file.set("texture", BaseDataGenerator.getTexture(type));
        file.set("name", "&" + type.getDefaultCustomName());
        file.set("lore", Collections.singletonList("&7Place me :D"));
        file.set("shop", true);
        file.set("price", 250);
        file.set("animation", true);
        file.set("animation_type", "MOBSPAWNER_FLAMES");
        file.set("craft.default", true);
        file.set("craft.custom", false);
    }

    @Override
    public void saveLuckyEntries(Config config, LuckyEntry... entries) {
        config.set("drop", null);
        for (int i = 0; i < entries.length; i++) {
            saveUsingLoader(config, "drop." + i, entries[i]);
        }
    }

    @Override
    public void saveUsingLoader(Config config, String path, LuckyEntry entry) {
        if (Templates.VERSION.hasJSONLoader()) {
            engine.getPathLoader().save(config, path, entry);
        } else {
            config.set(path, entry.stream().map(engine.getStringLoader()::serialize)
                    .filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    @Override
    public LuckyEntry[] generateLuckyEntries(int min, int max) {
        List<LuckyEntry> list = new ArrayList<>();
        int limit = ThreadLocalRandom.current().nextInt(min, max + 1);
        for (int i = 0; i < limit; i++) {
            list.add(generateLuckyEntry());
        }
        return list.toArray(new LuckyEntry[0]);
    }

    @Override
    public LuckyEntry generateLuckyEntry() {
        LuckyEntry entry = new LuckyEntry(DropChance.random());
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 5); i++) {
            LuckyDrop drop = generateLuckyDrop();
            if (drop == null) continue;
            entry.add(drop);
        }
        return entry;
    }

    @Override
    public LuckyDrop generateLuckyDrop() {
        int number = ThreadLocalRandom.current().nextInt(100) + 1;
        if (number <= 50) {
            return generateItem();
        } else if (number <= 80) {
            return generateEntity();
        } else if (number <= 90) {
            return generateSpecial();
        } else {
            return generateLuckyBlock();
        }
    }

    public LuckyDrop generateLuckyBlock() {
        LuckyBlockKey[] blockKeys = engine.getLoadedTypes();
        int index = ThreadLocalRandom.current().nextInt(blockKeys.length + 2);
        int amount = ThreadLocalRandom.current().nextInt(1, 5);
        if (index >= blockKeys.length) return new RandomLuckyItemDrop(amount);
        return new LuckyItemDrop(blockKeys[index], amount);
    }

    public LuckyDrop generateEntity() {
        List<EntityType> list = Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable).collect(Collectors.toList());
        int amount = ThreadLocalRandom.current().nextInt(1, 5);
        return new EntityDrop(list.get(ThreadLocalRandom.current().nextInt(list.size())), amount);
    }

    public LuckyDrop generateSpecial() {
        switch (ThreadLocalRandom.current().nextInt(6)) {
            case 0:
                return new WaterBucketSpecial(LuckyDrop.Special.WATER_BUCKET.defaultValue());
            case 1:
                return new TntExplosionSpecial(LuckyDrop.Special.TNT_EXPLOSION.defaultValue());
            case 2:
                return new TntColumnSpecial(LuckyDrop.Special.TNT_COLUMN.defaultValue());
            case 3:
                return new PigSpecial(ThreadLocalRandom.current().nextBoolean() ?
                        LuckyDrop.Special.PIG.defaultValue() : (LuckyDrop.Special.PIG.defaultValue()
                        + ThreadLocalRandom.current().nextInt(20)));
            case 4:
                return new LightningSpecial(LuckyDrop.Special.LIGHTNING.defaultValue());
            case 5:
                return new ExperienceExplosionSpecial(LuckyDrop.Special.EXPERIENCE_EXPLOSION.defaultValue());
        }
        return null;
    }

    public LuckyDrop generateItem() {
        List<Material> materials = Arrays.stream(Material.values())
                .filter(material -> {
                    try {
                        return material.isItem();
                    } catch (Throwable throwable) {
                        return true;
                    }
                })
                .filter(material -> !material.name().contains("AIR"))
                .filter(material -> !material.name().startsWith("LEGACY"))
                .unordered().collect(Collectors.toList());
        Material material = materials.get(ThreadLocalRandom.current().nextInt(materials.size()));
        if (material.isBlock()) {
            int amount;
            switch (ThreadLocalRandom.current().nextInt(4)) {
                case 0:
                    amount = 64;
                    break;
                case 1:
                    amount = 32;
                    break;
                case 2:
                    amount = 16;
                    break;
                default:
                    amount = 8;
                    break;
            }
            if (amount > material.getMaxStackSize())
                amount = material.getMaxStackSize();
            if (amount < 1)
                amount = 1;
            return new ItemDrop(new ItemStack(material, amount));
        }
        return new ItemDrop(new ItemStack(material,
                ThreadLocalRandom.current().nextInt(material.getMaxStackSize()) + 1));
    }

}
