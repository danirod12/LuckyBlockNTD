package me.DenBeKKer.ntdLuckyBlock.recipe;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.stream.Stream;

public class LuckyRecipe {

    private final LuckyRecipeItem[] items;
    private final LuckyBlockType type;
    private final boolean anyMatrix;
    private final String permission;

    public LuckyRecipe(LuckyBlockType type, LuckyRecipeItem[] items, String permission, boolean anyMatrix) {
        if (!anyMatrix && items.length != 9)
            throw new IllegalArgumentException("You should provide 9 LuckyRecipeItems");
        this.items = items;
        this.type = type;
        this.anyMatrix = anyMatrix;
        this.permission = permission;
    }

    public boolean verify(ItemStack[] origin) {
        if (origin.length != 9) {
            return false;
        }

        if (anyMatrix) {
            return verifyAny(origin);
        }

        for (int i = 0; i < origin.length; i++) {
            if (items[i] == null) {
                if (origin[i] == null) continue;
                return false;
            }
            if (!items[i].isMatch(origin[i])) {
                LBMain.debug("Item " + items[i].toString() + " not matches "
                        + (origin[i] == null ? "null" : origin[i].getType().name()));
                return false;
            }
        }
        return true;
    }

    public boolean verifyAny(ItemStack[] origin) {
        if (origin.length != 9)
            return false;

        ItemStack[] array = origin.clone();
        items:
        for (LuckyRecipeItem item : items) {
            if (item == null)
                continue;
            for (int i = 0; i < array.length; i++) {
                if (item.isMatch(array[i])) {
                    array[i] = null;
                    continue items;
                }
            }
            return false;
        }
        return Stream.of(array).noneMatch(Objects::nonNull);
    }

    public ItemStack getResult() throws LuckyBlockNotLoadedException {
        return type.get().getSkull();
    }

    public boolean hasAccess(Player player) {
        if (permission == null || player == null) return true;
        return Misc.hasPermission(player, permission);
    }
}
