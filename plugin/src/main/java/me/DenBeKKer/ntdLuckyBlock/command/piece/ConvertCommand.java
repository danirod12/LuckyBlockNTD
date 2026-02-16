package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.loader.ConvertFactory;
import org.bukkit.command.CommandSender;

public class ConvertCommand extends LBCommand {

    private final ConvertFactory convertFactory;

    public ConvertCommand(ConvertFactory convertFactory) {
        super(true, null, "convert");
        this.convertFactory = convertFactory;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        if (convertFactory.getRequests() == 0) {
            sender.sendMessage("§cThere is no convert requests");
            return CommandResponse.SUCCESS;
        }

        if (!LuckyBlockAPI.getVersionType().isPremium()) {
            sender.sendMessage("§cThis feature available only in premium plugin version");
            return CommandResponse.SUCCESS;
        }

        // premium command implementation
        throw new UnsupportedOperationException("Command not implemented. Hack attempt?");
    }
}
