package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;

@Getter
public class OppedDrop implements LuckyDrop {

    @SerializedName(value = "command")
    private final String command;

    /**
     * @param command - Command that will be executed
     */
    public OppedDrop(String command) {
        this.command = command;
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        if (execution.getPlayer() == null) {
            return;
        }
        Misc.performCommand(this.command, execution.getBlock(),
                execution.getPlayer(), Misc.PerformCommandAs.OPPED_PLAYER);
    }
}
