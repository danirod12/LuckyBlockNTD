package me.DenBeKKer.ntdLuckyBlock.api.setup;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ILuckyRecipeItem {

    boolean isMatch(ItemStack item);

    Type getType();

    Object getObject();

    enum Type {
        MATERIAL,
        LUCKY_BLOCK,
        DYE;

        public boolean isInstance(Object object) {
            switch (this) {
                case DYE:
                    return object instanceof DyeColor;
                case LUCKY_BLOCK:
                    return object == null || object instanceof LuckyBlockKey;
                case MATERIAL:
                    return object instanceof Material;
                default:
                    throw new RuntimeException();
            }
        }
    }
}
