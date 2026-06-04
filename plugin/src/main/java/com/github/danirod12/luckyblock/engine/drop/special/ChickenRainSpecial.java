package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.event.EntitySpawnEvent;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ChickenRainSpecial implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public ChickenRainSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        Location baseLocation = execution.getPlayer() == null
                ? execution.getBlock().getLocation() : execution.getPlayer().getLocation();

        List<Entity> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 10;
            double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 10;
            Location spawnLoc = baseLocation.clone().add(offsetX, 15, offsetZ);

            Entity chicken = Objects.requireNonNull(baseLocation.getWorld())
                    .spawnEntity(spawnLoc, EntityType.CHICKEN);
            list.add(chicken);
        }

        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(execution, list));
    }
}
