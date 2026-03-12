package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.util.ISpigotUpdater;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

public class UpdatesCommand extends LBCommand {

    private final ISpigotUpdater updater;

    public UpdatesCommand(ISpigotUpdater updater) {
        super(true, Message.CMD_CHECKFORUPDATES, "checkforupdates", "updates", "update");
        this.updater = updater;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§aChecking for an updates...");
        this.updater.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.updater.getPlugin(), () -> {
            updater.checkForUpdates(true, true);
            if (!updater.isNeedUpdate()) {
                sender.sendMessage("§aNo updates were found :(");
                if (!LuckyBlockAPI.getVersionType().isPremium()) {
                    sender.sendMessage("§bWant more features? Check out premium version!");
                }
            }
        });
        return CommandResponse.SUCCESS;
    }
}
