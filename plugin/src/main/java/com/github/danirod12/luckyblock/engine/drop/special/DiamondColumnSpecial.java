package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiamondColumnSpecial implements LuckyDrop {

    @SerializedName(value = "materials")
    private final List<Material> materials;

    public DiamondColumnSpecial(List<Material> materials) {
        this.materials = materials;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        if (materials == null) {
            return;
        }

        Block block = execution.getBlock();
        int y = materials.size() + 10;

        new BukkitRunnable() {

            private final List<Material> list = new ArrayList<>(materials);
            private int i = 4;

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (i == 4 && list.isEmpty()) {
                    block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, y, 0.5),
                            Material.DIAMOND_BLOCK, (byte) 0);
                    i = 3;
                    return;
                }

                if (i <= 3) {
                    i--;
                    if (i <= 0) {
                        Block b = block.getWorld().getBlockAt(block.getLocation().add(0.5, y - 9, 0.5));
                        if (b.getType() == Material.AIR) {
                            b.setType(Material.FIRE);
                        }
                        block.getWorld().strikeLightning(block.getLocation().add(0.5, y - 10, 0.5));
                        cancel();
                    }
                    return;
                }

                block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, y, 0.5), list.get(0), (byte) 0);
                list.remove(0);
            }
        }.runTaskTimer(execution.getInstance(), 10, 10);
    }
}
