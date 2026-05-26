package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.event.EntitySpawnEvent;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AnnoyingBabySpecial implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public AnnoyingBabySpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        Location location = execution.getPlayer() == null
                ? execution.getBlock().getLocation().add(0.5, 0.5, 0.5) : execution.getPlayer().getLocation();

        List<Entity> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.setBaby(true);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
            list.add(zombie);
        }

        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(execution, list));
    }
}
