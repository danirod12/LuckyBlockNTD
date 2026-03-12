package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.api.event.EntitySpawnEvent;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityDrop implements LuckyDrop {

    @SerializedName(value = "entity")
    private final EntityType entity;
    @SerializedName(value = "amount")
    private final int amount;

    /**
     * @param entity - EntityType of entity that will be spawned
     * @param amount - Entity amount that will be spawned
     */
    public EntityDrop(EntityType entity, int amount) {
        this.entity = entity;
        this.amount = amount;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Block block = execution.getBlock();
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(block.getWorld().spawnEntity(block.getLocation().add(0.5, 1, 0.5), entity));
        }
        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(execution, entities));
    }
}
