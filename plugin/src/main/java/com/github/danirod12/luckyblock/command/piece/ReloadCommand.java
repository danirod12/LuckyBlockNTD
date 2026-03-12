package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends LBCommand {

    private final LuckyBlockEngine engine;

    public ReloadCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_RELOAD, "reload", "restart", "reboot");
        this.engine = engine;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        long ms = System.currentTimeMillis();

        LuckyBlockAPI.reloadConfig();
        LuckyBlockAPI.reloadSystem();

        sender.sendMessage(Message.RELOADED_CONFIG.getAsString().replace("%amount%",
                String.valueOf(engine.getLoadedTypes().length)));
        engine.getLogChannel().info("Reloaded (took " + (System.currentTimeMillis() - ms) + " ms)... ");
        return CommandResponse.SUCCESS;
    }
}
