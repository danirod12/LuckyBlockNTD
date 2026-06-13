package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.folia.ManagedRunnable;
import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

@Getter
public class TntColumnSpecial implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public TntColumnSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        SchedulerManager.runTimerAt(execution.getInstance(), execution.getBlock().getLocation(), new ManagedRunnable() {
            int i = amount;

            @Override
            public void run() {

                if (i == amount) {
                    execution.getBlock().setType(Material.REDSTONE_BLOCK);
                }

                Location location = execution.getBlock().getLocation().add(.5, 4, .5);
                execution.getBlock().getWorld().spawnFallingBlock(location, Material.TNT, (byte) 0);
                if (--i <= 0) {
                    this.cancel();
                }
            }
        }, 10L, 10L);
    }
}
