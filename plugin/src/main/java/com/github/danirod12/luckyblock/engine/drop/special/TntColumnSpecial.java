package com.github.danirod12.luckyblock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class TntColumnSpecial implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public TntColumnSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        new BukkitRunnable() {
            int i = amount;

            @Override
            public void run() {

                if (i == amount) {
                    execution.getBlock().setType(Material.REDSTONE_BLOCK);
                }

                Location location = execution.getBlock().getLocation().add(.5, 4, .5);
                execution.getBlock().getWorld().spawnFallingBlock(location, Material.TNT, (byte) 0);
                if (--i <= 0) {
                    cancel();
                }
            }
        }.runTaskTimer(execution.getInstance(), 10, 10);
    }
}
