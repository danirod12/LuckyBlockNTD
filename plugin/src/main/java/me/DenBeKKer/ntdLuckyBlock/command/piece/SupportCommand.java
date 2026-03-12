package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.util.LogChannel;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Templates;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
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
