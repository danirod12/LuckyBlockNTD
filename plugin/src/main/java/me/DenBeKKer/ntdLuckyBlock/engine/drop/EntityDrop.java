package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.event.EntitySpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
    public void execute(LuckyBlockKey related, Block block, Player target) {
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(block.getWorld().spawnEntity(block.getLocation().add(0.5, 1, 0.5), entity));
        }
        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(related, entities, target));
    }

    public EntityType getEntityType() {
        return entity;
    }

    public int getAmount() {
        return amount;
    }
}
