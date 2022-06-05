package me.DenBeKKer.ntdLuckyBlock.recipe;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class LuckyRecipeItem {

    private final Type type;
    private Enum<?> object;

    public LuckyRecipeItem(Material material) {
        object = material;
        type = Type.MATERIAL;
    }

    public LuckyRecipeItem(DyeColor dye) {
        object = dye;
        type = Type.DYE;
    }

    public LuckyRecipeItem(LuckyBlockType luckyblock) {
        object = luckyblock;
        type = Type.LUCKYBLOCK;
    }

    public LuckyRecipeItem allowAnyLuckyBlock() {
        if (type == Type.LUCKYBLOCK)
            object = null;
        return this;
    }

    public boolean isMatch(ItemStack item) {

        if (item == null) return false;
        switch (type) {
            case DYE: {

                if (LBMain.getInstance().factory instanceof Mat1_12) {
                    if (!(item.getData() instanceof Dye)) return false;
                    return ((Dye) item.getData()).getColor() == object;
                } else {
                    return item.getType().name().equalsIgnoreCase(object.name() + "_DYE");
                }

            }
            case MATERIAL:
                return item.getType() == object;
            case LUCKYBLOCK: {

                if (object == null) {
                    return LuckyBlockAPI.checkLuckyBlock(item);
                } else return LuckyBlockAPI.parseLuckyBlock(item) == object;

            }
        }
        return false;

    }

    @Override
    public String toString() {
        return "{\"type\":\"" + type.name() + "\",\"option\":\"" + (object == null ? "null" : object.name()) + "\"}";
    }

    private enum Type {

        MATERIAL,
        LUCKYBLOCK,
        DYE;

    }

}
