package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.util.LogChannel;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.util.Templates;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SupportCommand extends LBCommand {

    private final LogChannel logChannel;

    public SupportCommand(LogChannel logChannel) {
        super(true, Message.CMD_SUPPORT, "support");
        this.logChannel = logChannel;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        boolean notifySender = sender instanceof Player;
        for (String entry : Templates.SUPPORT_INFO) {
            this.logChannel.info(entry);
            if (notifySender) {
                sender.sendMessage(entry);
            }
        }
        return CommandResponse.SUCCESS;
    }
}
