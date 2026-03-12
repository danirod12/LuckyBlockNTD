package com.github.danirod12.luckyblock.recipe;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipeItem;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.util.Optional;

public class LuckyRecipeItem implements ILuckyRecipeItem {

    private final LuckyBlockEngine engine;
    private final Type type;
    private final Object object;

    public LuckyRecipeItem(LuckyBlockEngine engine, Type type, Object object) {
        if (!type.isInstance(object)) {
            throw new IllegalArgumentException();
        }
        this.engine = engine;
        this.type = type;
        this.object = object;
    }

    @Override
    public boolean isMatch(ItemStack item) {
        if (item == null) {
            return false;
        }

        switch (type) {
            case DYE: {
                if (engine.getVersionControl().isLegacy()) {
                    if (!(item.getData() instanceof Dye)) {
                        return false;
                    }
                    return ((Dye) item.getData()).getColor() == object;
                } else {
                    return item.getType().name().equalsIgnoreCase(((Enum<?>) object).name() + "_DYE");
                }
            }
            case MATERIAL:
                return item.getType() == object;
            case LUCKY_BLOCK: {
                Optional<LuckyBlockKey> luckyBlockKey = engine.parseLuckyBlock(item);
                return luckyBlockKey.isPresent() && (object == null || object.equals(luckyBlockKey.get()));
            }
        }
        return false;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        String objectName;
        if (object == null) {
            objectName = "null";
        } else if (object instanceof Enum) {
            objectName = ((Enum<?>) object).name();
        } else if (object instanceof LuckyBlockKey) {
            objectName = ((LuckyBlockKey) object).getKey();
        } else {
            objectName = object.toString();
        }
        return "{\"type\":\"" + type.name() + "\",\"object\":\"" + objectName + "\"}";
    }
}
