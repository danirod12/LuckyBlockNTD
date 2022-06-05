package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class DestroyCommand implements LBPlayerCommand {

    @Override
    public boolean permission() {
        return true;
    }

    @Override
    public final String[] commands() {
        return new String[]{"destroy", "kill"};
    }

    @Override
    public Message helpMessage() {
        return Message.CMD_DESTROY;
    }

    @Override
    public CommandResponse execute(Player player, String label, String[] args) {

        z:
        if (args.length > 0) {

            boolean destroyAll = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("-all")) {
                    destroyAll = true;
                    break;
                }
            }

            int removed;
            if (args[0].equalsIgnoreCase("chunk")) {

                removed = LuckyBlockAPI.destroyEntity(player.getLocation().getChunk().getEntities(), destroyAll);

            } else {

                int r = 0;
                try {
                    r = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {
                }
                if (r <= 0) break z;

                List<Entity> list = player.getNearbyEntities(r, r, r);
                removed = LuckyBlockAPI.destroyEntity(list.toArray(new Entity[0]), destroyAll);

            }
            player.sendMessage(Message.CMD_DESTROYED_LB.getAsString(true).replace("%amount%", String.valueOf(removed)));
            return CommandResponse.SUCCESS;

        }
        return CommandResponse.SEND_HELP;

    }

}
