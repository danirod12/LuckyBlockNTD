package me.DenBeKKer.ntdLuckyBlock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ExperienceExplosionSpecial extends ExplosionableItems implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int a;

    public ExperienceExplosionSpecial(int a) {
        this.a = a;
    }

    public int getAmount() {
        return a;
    }

    @Override
    public void execute(LBMain.LuckyBlockType related, Block block, Player player) {

        throwExplosion(EntityType.THROWN_EXP_BOTTLE,
                player == null ? block.getLocation().add(.5, .5, .5) : player.getLocation().clone().add(.0, .5, .0), a);

    }

}
