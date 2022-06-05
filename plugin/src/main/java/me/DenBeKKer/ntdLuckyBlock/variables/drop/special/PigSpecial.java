package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.events.EntitySpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class PigSpecial implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int a;

    public PigSpecial(int a) {
        this.a = a;
    }

    public int getAmount() {
        return a;
    }

    @Override
    public void execute(Block b, Player target) {
        Pig current = null;
        for (int i = 0; i < a; i++) {
            Pig pig = (Pig) b.getWorld().spawnEntity(target.getLocation(), EntityType.PIG);
            pig.setCanPickupItems(false);
            pig.setRemoveWhenFarAway(true);
            if (current != null)
                current.setPassenger(pig);
            current = pig;
        }
        current.setPassenger(target);
    }

    @Override
    public void execute(LBMain.LuckyBlockType type, Block b, Player target) {

        Location location = target == null ? b.getLocation().add(0.5, 0.4, 0.5) : target.getLocation();

        Collection<Entity> collection = new ArrayList<>();
        Pig current = null;
        for (int i = 0; i < a; i++) {
            Pig pig = (Pig) b.getWorld().spawnEntity(location, EntityType.PIG);
            collection.add(pig);
            pig.setCanPickupItems(false);
            pig.setRemoveWhenFarAway(true);
            if (current != null)
                current.setPassenger(pig);
            current = pig;
        }

        if (target != null)
            current.setPassenger(target);

        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(type, collection, target));

    }

}
