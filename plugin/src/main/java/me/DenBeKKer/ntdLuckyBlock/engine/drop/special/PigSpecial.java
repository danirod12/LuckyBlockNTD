package me.DenBeKKer.ntdLuckyBlock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.event.EntitySpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PigSpecial implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    public PigSpecial(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(Execution execution) {
        Location location = execution.getPlayer() == null ?
                execution.getBlock().getLocation().add(0.5, 0.4, 0.5) : execution.getPlayer().getLocation();

        List<Entity> list = new ArrayList<>();
        Pig current = null;
        for (int i = 0; i < amount; i++) {
            Pig pig = (Pig) execution.getBlock().getWorld().spawnEntity(location, EntityType.PIG);
            list.add(pig);
            pig.setCanPickupItems(false);
            pig.setRemoveWhenFarAway(true);
            if (current != null) {
                current.setPassenger(pig);
            }
            current = pig;
        }

        if (execution.getPlayer() != null) {
            current.setPassenger(execution.getPlayer());
        }
        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(execution, list));
    }
}
