package com.github.danirod12.luckyblock.nms.material;

import com.github.danirod12.luckyblock.api.util.ColorData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Mat1_12 implements IMat {

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItem(Mat mat, int i) {
        switch (mat) {

            case PLAYER_SKULL:
                return new ItemStack(Material.valueOf("SKULL_ITEM"), i, (short) 3);
            case GRAY_PANE:
                return new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), i, ColorData.GRAY.getData());
            case BLACK_PANE:
                return new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), i, ColorData.BLACK.getData());
            case WHITE_WOOL:
                return new ItemStack(Material.valueOf("WOOL"), i, ColorData.WHITE.getData());

            case BEEF:
                return new ItemStack(Material.valueOf("RAW_BEEF"), i);

            default:
                return null;
        }
    }

    @Override
    public boolean isSkull(ItemStack item) {
        return item.getType() == Material.valueOf("SKULL_ITEM");
    }

    @Override
    public String build() {
        return "old";
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getGlass(ColorData color, int amount) {
        return new ItemStack(Material.valueOf("STAINED_GLASS"), amount, (short) color.ordinal());
    }

    @Override
    public boolean isSign(Material type) {
        return type.name().equalsIgnoreCase("SIGN");
    }

    @Override
    public boolean isSkull(Material type) {
        return type.name().contains("SKULL");
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInHand();
    }
}
