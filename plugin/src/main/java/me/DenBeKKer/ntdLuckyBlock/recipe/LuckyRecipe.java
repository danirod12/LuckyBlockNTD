package me.DenBeKKer.ntdLuckyBlock.recipe;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipeItem;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.stream.Stream;

public class LuckyRecipe implements ILuckyRecipe {

    private final ILuckyRecipeItem[] items;
    private final LuckyBlockKey type;
    private final boolean anyMatrix;
    private final String permission;

    public LuckyRecipe(LuckyBlockKey type, ILuckyRecipeItem[] items, String permission, boolean anyMatrix) {
        if (!anyMatrix && items.length != 9) {
            throw new IllegalArgumentException("You should provide 9 LuckyRecipeItems");
        }
        this.items = items;
        this.type = type;
        this.anyMatrix = anyMatrix;
        this.permission = permission;
    }

    @Override
    public int verify(ItemStack[] origin) {
        if (origin.length != 9) {
            return 0;
        }
        if (anyMatrix) {
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
        for (ILuckyRecipeItem item : items) {
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

    @Override
    public LuckyBlockKey getType() {
        return type;
    }

    @Override
    public ILuckyRecipeItem[] getItems() {
        return items;
    }

    @Override
    public boolean hasAccess(Player player) {
        if (permission == null || player == null) {
            return true;
        }
        return Misc.hasPermission(player, permission);
    }
}
