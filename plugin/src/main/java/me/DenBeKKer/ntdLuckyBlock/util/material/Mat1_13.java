package me.DenBeKKer.ntdLuckyBlock.util.material;

import me.DenBeKKer.ntdLuckyBlock.util.ColorData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Mat1_13 implements IMat {

    @Override
    public ItemStack getItem(Mat mat, int i) {
        switch (mat) {

            case PLAYER_SKULL:
                return new ItemStack(Material.valueOf("PLAYER_HEAD"), i);
            case GRAY_PANE:
                return new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"), i);
            case BLACK_PANE:
                return new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE"), i);
            case WHITE_WOOL:
                return new ItemStack(Material.valueOf("WHITE_WOOL"), i);

            case BEEF:
                return new ItemStack(Material.valueOf("BEEF"), i);

            default:
                return null;

        }
    }

    @Override
    public boolean isSkull(ItemStack item) {
        return item.getType() == Material.valueOf("PLAYER_HEAD");
    }

    @Override
    public String build() {
        return "main";
    }

    @Override
    public ItemStack getGlass(ColorData color, int amount) {
        return new ItemStack(Material.valueOf(color.name() + "_STAINED_GLASS"), amount);
    }

    @Override
    public boolean isSign(Material type) {
        return type.name().contains("SIGN");
    }

    @Override
    public boolean isSkull(Material type) {
        return type.name().contains("PLAYER") && type.name().contains("HEAD");
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

}
