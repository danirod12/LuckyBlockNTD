package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.util.Misc;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
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
