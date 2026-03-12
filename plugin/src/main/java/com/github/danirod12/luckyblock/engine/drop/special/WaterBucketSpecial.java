package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class WaterBucketSpecial implements LuckyDrop {

    @SerializedName(value = "height")
    private final int height;

    public WaterBucketSpecial(int height) {
        this.height = height;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Player target = execution.getPlayer();
        ItemStack itemStack = new ItemStack(Material.WATER_BUCKET);
        if (target == null) {
            execution.getBlock().getWorld().dropItem(execution.getBlock().getLocation(), itemStack);
        } else {
            target.sendMessage(Message.WATER_BUCKET.getAsString());
            target.getInventory().addItem(itemStack);
            target.teleport(target.getLocation().add(0, height, 0));
        }
    }
}
