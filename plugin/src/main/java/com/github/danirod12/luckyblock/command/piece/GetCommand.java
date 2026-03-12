package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.model.LuckyBlock;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBPlayerCommand;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class GetCommand extends LBPlayerCommand {

    private final LuckyBlockEngine engine;

    public GetCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_GET, "get");
        this.engine = engine;
    }

    @Override
    public CommandResponse execute(Player player, String label, String[] args) {
        if (args.length > 0) {
            LuckyBlockKey key;
            if (args[0].equalsIgnoreCase("random")) {
                LuckyBlockKey[] array = Arrays.stream(engine.getLoadedTypes())
                        .filter(k -> this.hasPermission(player, k)).toArray(LuckyBlockKey[]::new);
                key = array.length == 0 ? null : array[ThreadLocalRandom.current().nextInt(array.length)];
            } else {
                key = engine.getLoaded(args[0]).orElse(null);
            }

            if (key == null) {
                player.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[0]));
                return CommandResponse.SUCCESS;
            }
            LuckyBlock block = engine.get(key).orElseThrow(RuntimeException::new);
            if (!hasPermission(player, key)) {
                player.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString()
                        .replace("%lb%", block.getCustomName()));
                return CommandResponse.SUCCESS;
            }

            int amount = args.length < 2 ? 1 : Misc.parseInteger(args[1], 1);
            block.giveItem(player, amount);
            player.sendMessage(Message.CMD_LB_RECEIVED.getAsString().replace("%lb%", block.getCustomName())
                    .replace("%amount%", String.valueOf(amount)));
            return CommandResponse.SUCCESS;
        }
        return CommandResponse.SEND_HELP;
    }

    private boolean hasPermission(Player player, LuckyBlockKey key) {
        if (this.engine.getConfigHolder().getCommandPermissionsForEach) {
            return player.hasPermission("luckyblock.command.get." + key);
        } else {
            return player.hasPermission("luckyblock.command.get");
        }
    }
}
