package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ConsoleDrop implements LuckyDrop {

    @SerializedName(value = "command")
    private final String cmd;

    /**
     * @param command - Command that will be executed
     */
    public ConsoleDrop(String command) {
        this.cmd = command;
    }

    @Override
    public void execute(LuckyBlockKey related, Block b, Player player) {
        Misc.performCommand(this.cmd, b, player, Misc.PerformCommandAs.CONSOLE);
    }

    public String getCommand() {
        return cmd;
    }
}
