package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
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
