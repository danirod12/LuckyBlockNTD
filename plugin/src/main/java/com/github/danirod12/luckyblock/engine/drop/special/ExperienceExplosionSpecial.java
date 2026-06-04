package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.util.bukkit.ItemsExplosion;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.entity.EntityType;

@Getter
public class ExperienceExplosionSpecial extends ItemsExplosion implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public ExperienceExplosionSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        throwExplosion(EntityType.THROWN_EXP_BOTTLE, execution.getPlayer() == null
                ? execution.getBlock().getLocation().add(.5, .5, .5)
                : execution.getPlayer().getLocation().clone().add(.0, .5, .0), amount);
    }
}
