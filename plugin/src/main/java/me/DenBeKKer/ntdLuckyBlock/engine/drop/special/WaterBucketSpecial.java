package me.DenBeKKer.ntdLuckyBlock.engine.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WaterBucketSpecial implements LuckyDrop {

    @SerializedName(value = "height")
    private final int h;

    public WaterBucketSpecial(int h) {
        this.h = h;
    }

    public int getHeight() {
        return h;
    }

    @Override
    public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
        if (target == null) {
            b.getWorld().dropItem(b.getLocation(), new ItemStack(Material.WATER_BUCKET));
        } else {
            target.sendMessage(Message.WATER_BUCKET.getAsString());
            target.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
            target.teleport(target.getLocation().add(0, h, 0));
        }
    }

}
