package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.event.ItemSpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class RandomLuckyItemDrop implements LuckyDrop {

    @SerializedName(value = "amount")
    private final int amount;

    /**
     * @param amount - Amount
     */
    public RandomLuckyItemDrop(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        LuckyBlockKey key = LuckyBlockAPI.getLuckyEngineProvider().random();
        if (key instanceof LuckyBlockKey.NotLoadedLuckyBlockKey)
            return;
        ItemStack stack = LuckyBlockAPI.getLuckyEngineProvider().get(key).orElseThrow(RuntimeException::new).getItem();
        if (stack == null) {
            return;
        }
        stack.setAmount(amount);

        Block block = execution.getBlock();
        Item item = block.getWorld().dropItem(block.getLocation().add(.5, .4, .5), stack);
        Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(execution, item));
    }
}
