package me.DenBeKKer.ntdLuckyBlock.factory;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.loader.JSONLoader;
import me.DenBeKKer.ntdLuckyBlock.loader.LegacyLoader;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.EntityDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.LuckyItemDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.RandomLuckyItemDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.special.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LBFactoryV2 implements LBFactory {

    @Override
    public void generate(Config config, LBMain.LuckyBlockType type, int min, int max) {
        LBMain.log(Level.INFO, "\u00a7fGenerating \u00a7" + type.toColorSymbol() + type.name()
                + " LuckyBlock\u00a7f configuration \u00a76(LBF version: 2)");
        FileConfiguration file = config.get();
        file.set("texture", type.getTexture());
        file.set("name", "&" + type.toColorSymbol() + String.valueOf(type.name().toCharArray()[0]).toUpperCase()
                + type.name().substring(1).toLowerCase().replace("_", " ") + " LuckyBlock");
        file.set("lore", Collections.singletonList("&7Place me :D"));
        file.set("eco", true);
        file.set("shop", true);
        file.set("price", 250);
        file.set("animation", true);
        file.set("animation_type", "MOBSPAWNER_FLAMES");
        file.set("craft.default", true);
        file.set("craft.custom", false);

        file.set("drop", null);
        if (LBMain.getVersionType().hasJSONLoader()) {
            JSONLoader loader = ((JSONLoader) LuckyBlockAPI.getJSONLoader());
            int limit = ThreadLocalRandom.current().nextInt(min, max + 1);
            for (int i = 0; i < limit; i++) {
                loader.save(config, "drop." + i, generateLuckyEntry());
            }
        } else {
            LegacyLoader loader = ((LegacyLoader) LuckyBlockAPI.getLegacyLoader());
            int limit = ThreadLocalRandom.current().nextInt(min, max + 1);
            for (int i = 0; i < limit; i++) {
                config.set("drop." + i, generateLuckyEntry().stream().map(loader::save)
                        .filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }

    }

    public LuckyEntry generateLuckyEntry() {
        LuckyEntry entry = new LuckyEntry(DropChance.random());
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 5); i++) {
            entry.add(generateLuckyDrop());
        }
        return entry;
    }

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
        int index = ThreadLocalRandom.current().nextInt(LBMain.LuckyBlockType.values().length + 2);
        int amount = ThreadLocalRandom.current().nextInt(1, 5);
        if (index >= LBMain.LuckyBlockType.values().length) return new RandomLuckyItemDrop(amount);
        return new LuckyItemDrop(LBMain.LuckyBlockType.values()[index], amount);
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
            return new ItemDrop(new ItemStack(material, amount));
        }
        return new ItemDrop(new ItemStack(material, ThreadLocalRandom.current().nextInt(material.getMaxStackSize())));
    }

}
