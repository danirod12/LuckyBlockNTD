package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;

@Getter
public class CommandDrop implements LuckyDrop {

    @SerializedName(value = "command")
    private final String command;

    /**
     * @param command - Command that will be executed
     */
    public CommandDrop(String command) {
        this.command = command;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Misc.performCommand(this.command, execution.getBlock(), execution.getPlayer(), Misc.PerformCommandAs.PLAYER);
    }
}
