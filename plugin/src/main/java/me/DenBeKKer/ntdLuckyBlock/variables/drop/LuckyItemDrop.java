package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.events.ItemSpawnEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyItemDrop implements LuckyDrop {

    @SerializedName(value = "type")
    private final LuckyBlockType item;
    @SerializedName(value = "amount")
    private final int amount;

    /**
     * @param type   - LuckyBlockType will be dropped
     * @param amount - LuckyBlock's amount
     */
    public LuckyItemDrop(LuckyBlockType type, int amount) {
        this.item = type;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public LuckyBlockType getType() {
        return item;
    }

    @Override
    public void execute(LuckyBlockType related, Block b, Player target) {
        if (item.isLoaded()) {
            ItemStack stack = LuckyBlockType.map().get(this.item).getSkull();
            stack.setAmount(amount);
            Item item = b.getWorld().dropItem(b.getLocation().add(.5, .4, .5), stack);
            Bukkit.getPluginManager().callEvent(new ItemSpawnEvent(related, item, target));
        }
    }

}
