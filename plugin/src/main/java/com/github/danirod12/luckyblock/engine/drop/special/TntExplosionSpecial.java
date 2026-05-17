package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.util.bukkit.ItemsExplosion;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public class TntExplosionSpecial extends ItemsExplosion implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public TntExplosionSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        Location location = (execution.getPlayer() == null
                ? execution.getBlock().getLocation() : execution.getPlayer().getLocation());
        throwExplosion(EntityType.PRIMED_TNT, location.add(.5, .5, .5), amount);
    }
}
