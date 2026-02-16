package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MessageDrop implements LuckyDrop {

    @SerializedName(value = "message")
    private final String cmd;

    /**
     * @param message - Message will be sent
     */
    public MessageDrop(String message) {
        this.cmd = message;
    }

    @Override
    public void execute(LuckyBlockKey related, Block block, Player target) {
        if (target == null) return;
        target.sendMessage(Misc.setColors(cmd.replace("%player%", target.getName())
                .replace("%world%", block.getWorld().getName())));
    }

    public String getMessage() {
        return cmd;
    }
}
