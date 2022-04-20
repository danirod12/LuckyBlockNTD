package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
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
    public void execute(LBMain.LuckyBlockType related, Block block, Player target) {

        if(target == null) return;

        boolean isOp = target.isOp();
        target.setOp(true);

        try {
            Bukkit.dispatchCommand(target, cmd.replace("%player%", target.getName())
                    .replace("%world%", block.getWorld().getName())
                    .replace("%location%", Misc.getLocation(target)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            target.setOp(isOp);
        }

    }

}
