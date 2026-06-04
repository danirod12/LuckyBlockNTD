package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.event.EntitySpawnEvent;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import java.util.ArrayList;
import java.util.List;

@Getter
public class JebSpecial implements SpecialLuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public JebSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        Location location = execution.getPlayer() == null
                ? execution.getBlock().getLocation().add(0.5, 0.5, 0.5) : execution.getPlayer().getLocation();

        List<Entity> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Sheep sheep = (Sheep) execution.getBlock().getWorld().spawnEntity(location, EntityType.SHEEP);
            sheep.setCustomName("jeb_");
            sheep.setCustomNameVisible(true);
            list.add(sheep);
        }

        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(execution, list));
    }
}
