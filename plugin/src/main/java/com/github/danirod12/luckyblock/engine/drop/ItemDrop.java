package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.event.ItemSpawnEvent;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.google.gson.annotations.SerializedName;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
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
    public void execute(LuckyDrop.Execution execution) {
        Block block = execution.getBlock();
        Item drop = block.getWorld().dropItem(block.getLocation().add(0.5, 0.4, 0.5), item);
        // 1.8 fix
        if (drop.getItemStack().getType() != item.getType()) {
            drop.remove();
            LuckyBlockAPI.getLogger().log(Level.WARNING,
                    execution.getKey() + " have corrupted item " + item.getType()
                            + ", remove it manually");
            execution.getKey().getSetup().ifPresent(instance -> {
                instance.getItemsBag().removeIf(collection -> {
                    collection.removeIf(luckyDrop -> luckyDrop instanceof ItemDrop
                            && ((ItemDrop) luckyDrop).item.equals(item));
                    return collection.isEmpty();
                });
                // TODO auto-remove? ( save ? )
            });
            return;
        }
        Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(execution, drop));
    }

    public static LuckyDrop deserialize(String[] data) {
        return new ItemDrop(NBT.itemStackFromNBT(NBT.parseNBT(data[0])));
    }

    public static String[] serialize(ItemDrop drop) {
        return new String[] {NBT.itemStackToNBT(drop.item).toString()};
    }
}
