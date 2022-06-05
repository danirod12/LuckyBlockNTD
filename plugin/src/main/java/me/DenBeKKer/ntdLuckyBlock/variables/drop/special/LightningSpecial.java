package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LightningSpecial implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int a;

    public LightningSpecial(int a) {
        this.a = a;
    }

    public int getAmount() {
        return a;
    }

    @Override
    public void execute(LBMain.LuckyBlockType related, Block b, Player target) {

        if (target == null) {
            b.getWorld().strikeLightning(b.getLocation().add(0.5, 0.5, 0.5));
        } else {

            new BukkitRunnable() {

                int i = 0;

                @Override
                public void run() {

                    if (i >= a || !target.isOnline() || target.isDead()) {
                        cancel();
                        return;
                    }
                    b.getWorld().strikeLightning(target.getLocation());
                    i++;

                }

            }.runTaskTimer(LBMain.getInstance(), 15, 15);

        }

    }

}
