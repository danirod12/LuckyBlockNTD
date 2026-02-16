package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.SpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class UpdatesCommand extends LBCommand {

    private final Plugin plugin;
    private final SpigotUpdater updater;

    public UpdatesCommand(Plugin plugin, SpigotUpdater updater) {
        super(true, Message.CMD_CHECKFORUPDATES, "checkforupdates", "updates", "update");
        this.plugin = plugin;
        this.updater = updater;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§aChecking for an updates...");
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
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
