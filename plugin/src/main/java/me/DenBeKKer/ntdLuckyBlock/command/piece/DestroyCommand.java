package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DestroyCommand extends LBPlayerCommand {

    private LuckyBlockEngine engine;

    public DestroyCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_DESTROY, "destroy", "kill");
        this.engine = engine;
    }

    @Override
    public CommandResponse execute(Player player, String label, String[] args) {
        if (args.length > 0) {
            boolean destroyAll = false;
            boolean destroyUnloaded = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("-all")) {
                    destroyAll = true;
                }
                if (arg.equalsIgnoreCase("-unloaded")) {
                    destroyUnloaded = true;
                }
            }

            Entity[] entities;
            if (args[0].equalsIgnoreCase("chunk")) {
                entities = player.getLocation().getChunk().getEntities();
            } else {
                int radius = 0;
                try {
                    radius = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {
                }
                if (radius <= 0) {
                    return CommandResponse.SEND_HELP;
                }
                entities = player.getNearbyEntities(radius, radius, radius).toArray(new Entity[0]);
            }
            int removed = entities.length - engine.destroyEntities(destroyAll, destroyUnloaded, entities).size();
            player.sendMessage(Message.CMD_DESTROYED_LB.getAsString(true).replace("%amount%",
                    removed + "/" + entities.length));
            return CommandResponse.SUCCESS;

        }
        return CommandResponse.SEND_HELP;
    }
}
