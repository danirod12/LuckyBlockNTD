package me.DenBeKKer.ntdLuckyBlock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TntColumnSpecial implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int a;

    public TntColumnSpecial(int a) {
        this.a = a;
    }

    public int getAmount() {
        return a;
    }

    @Override
    public void execute(LBMain.LuckyBlockType source, Block b, Player p) {

        new BukkitRunnable() {

            int i = a;

            @Override
            public void run() {

                if (i == a)
                    b.setType(Material.REDSTONE_BLOCK);

                b.getWorld().spawnFallingBlock(b.getLocation().add(.5, 4, .5), Material.TNT, (byte) 0);
                i--;
                if (i <= 0)
                    cancel();

            }

        }.runTaskTimer(LBMain.getInstance(), 10, 10);

    }

}
