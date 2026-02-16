package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class OppedDrop implements LuckyDrop {

    @SerializedName(value = "command")
    private final String cmd;

    /**
     * @param cmd - Command that will be executed
     */
    public OppedDrop(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public void execute(LuckyBlockKey related, Block block, Player target) {
        if (target == null)
            return;

        boolean isOp = target.isOp();
        target.setOp(true);

        try {
            Bukkit.dispatchCommand(target, cmd.replace("%world%", block.getWorld().getName())
                    .replace("%block_location%", Misc.getLocation(block.getLocation().add(.5D, .5D, .5D)))
                    .replace("%player%", target.getName())
                    .replace("%player_location%", Misc.getLocation(target.getLocation())));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            target.setOp(isOp);
        }
    }

    public String getCommand() {
        return cmd;
    }
}
