package me.DenBeKKer.ntdLuckyBlock.nms.material;

import me.DenBeKKer.ntdLuckyBlock.api.util.ColorData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IMat {

    List<Material> WOOLS = Stream.of(Material.values())
            .filter(n -> !n.name().startsWith("LEGACY_") && n.name().contains("WOOL"))
            .collect(Collectors.toList());

    static void setData(Block block, byte data) {
        try {
            block.getClass().getDeclaredMethod("setData", byte.class).invoke(block, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ItemStack getItem(Mat mat, int amount);

    ItemStack getGlass(ColorData color, int amount);

    boolean isSkull(ItemStack item);

    String build();

    boolean isSign(Material type);

    boolean isSkull(Material type);

    ItemStack getItemInMainHand(Player player);

    enum Mat {
        PLAYER_SKULL,
        GRAY_PANE,
        BLACK_PANE,
        WHITE_WOOL,
        BEEF
    }
}
