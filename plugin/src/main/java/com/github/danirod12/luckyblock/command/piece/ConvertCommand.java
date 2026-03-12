package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.engine.loader.ConvertFactory;
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
