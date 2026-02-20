package me.DenBeKKer.ntdLuckyBlock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class TntExplosionSpecial extends ExplosionableItems implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public TntExplosionSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        Location location = (execution.getPlayer() == null ?
                execution.getBlock().getLocation() : execution.getPlayer().getLocation());
        throwExplosion(EntityType.PRIMED_TNT, location.add(.5, .5, .5), amount);
    }
}
