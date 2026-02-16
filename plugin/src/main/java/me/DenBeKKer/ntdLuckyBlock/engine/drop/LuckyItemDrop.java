package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.event.ItemSpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyItemDrop implements LuckyDrop {

    @SerializedName(value = "type")
    private final LuckyBlockKey type;
    @SerializedName(value = "amount")
    private final int amount;

    /**
     * @param type   - LuckyBlockKey will be dropped
     * @param amount - LuckyBlock's amount
     */
    public LuckyItemDrop(LuckyBlockKey type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public LuckyBlockKey getKey() {
        return type;
    }

    @Override
    public void execute(LuckyBlockKey related, Block block, Player target) {
        LuckyBlockAPI.getLuckyEngineProvider().get(type).ifPresent(instance -> {
            ItemStack stack = instance.getItem();
            if (stack != null) {
                stack.setAmount(amount);
                Item item = block.getWorld().dropItem(block.getLocation().add(.5, .4, .5), stack);
                Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(related, item, target));
            }
        });
    }
}
