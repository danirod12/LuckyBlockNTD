package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.util.ISpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Templates;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class VersionCommand extends LBCommand {

    private final ISpigotUpdater spigotUpdater;
    private final String nmsVersion;

    public VersionCommand(ISpigotUpdater spigotUpdater, String nmsVersion) {
        super(false, null, "version", "ver", "v", "build", "debug", "about", "info");
        this.spigotUpdater = spigotUpdater;
        this.nmsVersion = nmsVersion;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§7[§eLuckyBlock§7] §fThis server is running §eLuckyBlock NTD");
        sender.sendMessage("§8 • §fRunning version - §e" + LuckyBlockAPI.getVersion() + " §7(" +
                LuckyBlockAPI.getVersionType().getColoredSimpleName() + "§7) " + (spigotUpdater.isNeedUpdate()
                ? "§c§l[!] §cVersion " + spigotUpdater.getLatestVersion() + " available" : "§aLatest version"));
        sender.sendMessage("§8 • §fBuild - §e" + LuckyBlockAPI.getBuild()
                + "§f, last update - §e" + LuckyBlockAPI.getLastUpdate());
        sender.sendMessage("§8 • §fPlugin author -§e danirod12 §7(aka Den Bekker)");

        for (String arg : args) {
            if (arg.equals("-full") || arg.equals("-f")) {
                // premium download info
                sender.sendMessage("§8 • §fPlatform - §e" + Bukkit.getVersion() + " §7(" + nmsVersion + ")");
            }
        }

        sender.sendMessage("§8 • §fSpigotMC url - §b" + spigotUpdater.getResourceURL());
        sender.sendMessage("§8 • §fbStats metrics -§b " + Templates.BSTATS_URL);
        return CommandResponse.SUCCESS;
    }
}
