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
    private final boolean any_matrix;
    private final String permission;

    public LuckyRecipe(LuckyBlockType type, LuckyRecipeItem[] items, String permission, boolean anymatrix) {

        if (!anymatrix && items.length != 9)
            throw new IllegalArgumentException("You should provide 9 LuckyRecipeItems");

        if (!type.isLoaded()) throw new UnsupportedOperationException("LuckyBlockType " + type.name() + " is unloaded");

        this.items = items;
        this.type = type;
        this.any_matrix = anymatrix;
        this.permission = permission;

    }

    public int verify(ItemStack[] origin) {
        if (origin.length != 9) {
            return 0;
        }
        if (any_matrix) {
            return verifyAny(origin);
        }

        int minAmount = 64;
        for (int i = 0; i < origin.length; i++) {
            if (items[i] == null) {
                if (origin[i] == null) {
                    continue;
                }
                return 0;
            }
            if (!items[i].isMatch(origin[i])) {
                return 0;
            }
            if (origin[i].getAmount() < minAmount) {
                minAmount = origin[i].getAmount();
            }
        }
        return minAmount;
    }

    private int verifyAny(ItemStack[] origin) {
        if (origin.length != 9) {
            return 0;
        }

        ItemStack[] array = origin.clone();
        int minAmount = 64;
        items:
        for (LuckyRecipeItem item : items) {
            if (item == null) {
                continue;
            }
            for (int i = 0; i < array.length; i++) {
                if (item.isMatch(array[i])) {
                    if (array[i].getAmount() < minAmount) {
                        minAmount = array[i].getAmount();
                    }
                    array[i] = null;
                    continue items;
                }
            }
            return 0;
        }
        return Stream.of(array).noneMatch(Objects::nonNull) ? minAmount : 0;
    }

    public ItemStack getResult() throws LuckyBlockNotLoadedException {
        return type.get().getSkull();
    }

    public boolean hasAccess(Player player) {
        if (permission == null || player == null) return true;
        return Misc.hasPermission(player, permission);
    }

}
