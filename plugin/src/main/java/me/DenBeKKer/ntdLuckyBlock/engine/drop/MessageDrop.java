package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.entity.Player;

public class MessageDrop implements LuckyDrop {

    @SerializedName(value = "message")
    private final String message;

    /**
     * @param message - Message will be sent
     */
    public MessageDrop(String message) {
        this.message = message;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Player target = execution.getPlayer();
        if (target == null) {
            return;
        }
        target.sendMessage(Misc.setColors(message.replace("%player%", target.getName())
                .replace("%world%", execution.getBlock().getWorld().getName())));
    }
}
