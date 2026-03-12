package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.util.Misc;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ConsoleDrop implements LuckyDrop {

    @SerializedName(value = "command")
    private final String command;

    /**
     * @param command - Command that will be executed
     */
    public ConsoleDrop(String command) {
        this.command = command;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Misc.performCommand(this.command, execution.getBlock(), execution.getPlayer(), Misc.PerformCommandAs.CONSOLE);
    }
}
