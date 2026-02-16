package me.DenBeKKer.ntdLuckyBlock.command.base;

import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class LBPlayerCommand extends LBCommand {

    public LBPlayerCommand(boolean permission, MessagesManager.Message helpMessage, String... commands) {
        super(true, permission, helpMessage, commands);
    }

    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        return execute((Player) sender, label, args);
    }

    public abstract CommandResponse execute(Player player, String label, String[] args);
}
