package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class TntExplosionSpecial extends ExplosionableItems implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int a;

    public TntExplosionSpecial(int a) {
        this.a = a;
    }

    public int getAmount() {
        return a;
    }

    @Override
    public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
        throwExplosion(EntityType.PRIMED_TNT, (target == null ? b.getLocation() : target.getLocation()).add(.5, .5, .5), a);
    }

}
