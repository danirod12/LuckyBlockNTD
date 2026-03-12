package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.event.ItemSpawnEvent;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

@Getter
public class LuckyItemDrop implements LuckyDrop {

    @SerializedName(value = "type")
    private final LuckyBlockKey key;
    @SerializedName(value = "amount")
    private final int amount;

    /**
     * @param key    - LuckyBlockKey will be dropped
     * @param amount - LuckyBlock's amount
     */
    public LuckyItemDrop(LuckyBlockKey key, int amount) {
        this.key = key;
        this.amount = amount;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        LuckyBlockAPI.getLuckyEngineProvider().get(key).ifPresent(instance -> {
            ItemStack stack = instance.getItem();
            if (stack != null) {
                stack.setAmount(amount);
                Block block = execution.getBlock();
                Item item = block.getWorld().dropItem(block.getLocation().add(.5, .4, .5), stack);
                Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(execution, item));
            }
        });
    }
}
