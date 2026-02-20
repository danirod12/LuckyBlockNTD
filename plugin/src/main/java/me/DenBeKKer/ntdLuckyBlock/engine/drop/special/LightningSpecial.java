package me.DenBeKKer.ntdLuckyBlock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LightningSpecial implements LuckyDrop {

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
            new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    if (i++ >= amount || !target.isOnline() || target.isDead()) {
                        cancel();
                        return;
                    }
                    block.getWorld().strikeLightning(target.getLocation());
                }
            }.runTaskTimer(execution.getInstance(), 15, 15);
        }
    }
}
