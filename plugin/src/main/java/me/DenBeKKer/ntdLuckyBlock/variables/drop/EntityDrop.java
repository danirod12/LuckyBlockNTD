package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.events.EntitySpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

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
    public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
        Collection<Entity> collection = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            collection.add(b.getWorld().spawnEntity(b.getLocation().add(0.5, 1, 0.5), entity));
        }
        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(related, collection, target));
    }

    public EntityType getEntityType() {
        return entity;
    }

    public int getAmount() {
        return amount;
    }

}
