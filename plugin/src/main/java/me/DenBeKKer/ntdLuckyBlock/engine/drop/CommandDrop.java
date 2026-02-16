package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CommandDrop implements LuckyDrop {

    @SerializedName(value = "command")
    private final String cmd;

    /**
     * @param command - Command that will be executed
     */
    public CommandDrop(String command) {
        this.cmd = command;
    }

    @Override
    public void execute(LuckyBlockKey related, Block block, Player player) {
        String cmd = this.cmd.replace("%world%", block.getWorld().getName())
                .replace("%block_location%", Misc.getLocation(block.getLocation().add(.5D, .5D, .5D)));
        if (cmd.contains("%player%") || cmd.contains("%player_location%")) {
            if (player == null)
                return;
            cmd = cmd.replace("%player%", player.getName())
                    .replace("%player_location%", Misc.getLocation(player.getLocation()));
        }
        Bukkit.dispatchCommand(player == null ? Bukkit.getConsoleSender() : player, cmd);
    }

    public String getCommand() {
        return cmd;
    }
}
