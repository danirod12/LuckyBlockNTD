package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.folia.ManagedRunnable;
import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
public class LightningSpecial implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public LightningSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Player target = execution.getPlayer();
        Block block = execution.getBlock();
        if (target == null) {
            block.getWorld().strikeLightning(block.getLocation().add(0.5, 0.5, 0.5));
        } else {
            SchedulerManager.runTimerAt(execution.getInstance(), block.getLocation(), new ManagedRunnable() {
                int i = 0;

                @Override
                public void run() {
                    if (i++ >= amount || !target.isOnline() || target.isDead()) {
                        this.cancel();
                        return;
                    }
                    block.getWorld().strikeLightning(target.getLocation());
                }
            }, 15L, 15L);
        }
    }
}
