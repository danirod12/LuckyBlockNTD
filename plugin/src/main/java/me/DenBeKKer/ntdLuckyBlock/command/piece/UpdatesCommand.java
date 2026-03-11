package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.util.ISpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
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
