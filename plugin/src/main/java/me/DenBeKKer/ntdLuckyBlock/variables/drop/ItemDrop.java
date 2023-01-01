package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.events.ItemSpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class ItemDrop implements LuckyDrop {

    @SerializedName(value = "item")
    private final ItemStack item;

    /**
     * @param item - ItemStack will be dropped
     */
    public ItemDrop(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItemCopy() {
        return item.clone();
    }

    @Override
    public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
        Item drop = b.getWorld().dropItem(b.getLocation().add(0.5, 0.4, 0.5), item);
        // 1.8 fix
        if (drop.getItemStack().getType() != item.getType()) {
            drop.remove();
            MvLogger.log(Level.WARNING, related + " have corrupted item " + item.getType()
                    + ", remove it manually");
            LBMain.LuckyBlockType.map().get(related).remove(this);
            return;
        }
        Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(related, drop, target));
    }

}
