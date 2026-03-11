package me.DenBeKKer.ntdLuckyBlock.command.base;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

@Getter
public abstract class LBCommand {

    private final boolean onlyPlayer;
    private final boolean permission;
    private final Message helpMessage;
    private final String[] commands;

    public LBCommand(boolean permission, Message helpMessage, String... commands) {
        this(false, permission, helpMessage, commands);
    }

    public LBCommand(boolean onlyPlayer, boolean permission, Message helpMessage, String... commands) {
        this.onlyPlayer = onlyPlayer;
        this.permission = permission;
        this.helpMessage = helpMessage;
        this.commands = commands;
    }

    public abstract CommandResponse execute(CommandSender sender, String label, String[] args);
}
