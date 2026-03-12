package com.github.danirod12.luckyblock.recipe;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.provider.LuckyRecipeProvider;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipe;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipeItem;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.engine.manager.BaseDataGenerator;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LuckyRecipeProviderImpl implements LuckyRecipeProvider {

    private final LuckyBlockEngine engine;

    public LuckyRecipeProviderImpl(LuckyBlockEngine engine) {
        this.engine = engine;
    }

    @Override
    public ILuckyRecipeItem createItem(Material material) {
        return new LuckyRecipeItem(engine, ILuckyRecipeItem.Type.MATERIAL, material);
    }

    @Override
    public ILuckyRecipeItem createItem(DyeColor color) {
        if (engine.getVersionControl().isModern()) {
            return createItem(Material.valueOf(color.name() + "_DYE"));
        }
        return new LuckyRecipeItem(engine, ILuckyRecipeItem.Type.DYE, color);
    }

    @Override
    public ILuckyRecipeItem createItem(LuckyBlockKey type) {
        return new LuckyRecipeItem(engine, ILuckyRecipeItem.Type.LUCKY_BLOCK, type);
    }

    @Override
    public ILuckyRecipeItem createItem(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        if (itemStack.getType().name().contains("DYE")) {
            if (engine.getVersionControl().isModern()) {
                return createItem(itemStack.getType());
            }
            //noinspection all
            return createItem(((Dye) itemStack.getData()).getColor());
        }
        Optional<LuckyBlockKey> luckyBlockKey = engine.parseLuckyBlock(itemStack);
        if (luckyBlockKey.isPresent()) {
            return createItem(luckyBlockKey.get());
        } else {
            return createItem(itemStack.getType());
        }
    }

    @Override
    public ILuckyRecipeItem createItemAnyLuckyBlockType() {
        return createItem((LuckyBlockKey) null);
    }

    @Override
    public ILuckyRecipe createRecipe(LuckyBlockKey related, ILuckyRecipeItem[] items,
                                     String permission, boolean anyMatrix) {
        return new LuckyRecipe(related, items, permission, anyMatrix);
    }

    @Override
    public List<ILuckyRecipe> getDefaultCrafts(LuckyBlockKey key) {
        List<ILuckyRecipe> list = new ArrayList<>();

        if (!BaseDataGenerator.hasDefaultCrafts(key)) {
            return list;
        }
        ILuckyRecipeItem goldIngot = createItem(Material.GOLD_INGOT);
        list.add(new LuckyRecipe(key, new ILuckyRecipeItem[]{
                goldIngot, goldIngot, goldIngot, goldIngot,
                createItem(key.getColorData().asDyeColor()),
                goldIngot, goldIngot, goldIngot, goldIngot
        }, "luckyblock.craft." + key.getKey().toLowerCase(), false));

        list.add(new LuckyRecipe(key, new ILuckyRecipeItem[]{
                createItem(key.getColorData().asDyeColor()),
                createItemAnyLuckyBlockType()
        }, "luckyblock.dye." + key.getKey().toLowerCase(), true));
        return list;
    }

    @Override
    public List<ILuckyRecipe> getCustomCrafts(LuckyBlockKey type) {
        engine.getLogChannel().warning("Custom crafts feature available only for premium version");
        engine.getLogChannel().warning("Check out premium plugin version - https://www.spigotmc.org/resources/94872");
        return new ArrayList<>();
    }
}
