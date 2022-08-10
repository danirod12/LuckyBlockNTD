package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.factory.LBFactory;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import org.bukkit.command.CommandSender;

public class GenerateCommand implements LBCommand {

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean permission() {
        return true;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(MessagesManager.Message.CMD_GENERATE_DESCRIPTION.getAsString(true));
            return CommandResponse.SUCCESS;
        }

        LBMain.LuckyBlockType type = LBMain.LuckyBlockType.parse(args[0]);
        if (type == null) {
            sender.sendMessage(MessagesManager.Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[0]));
            return CommandResponse.SUCCESS;
        }

        int factory;
        try {
            factory = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
            factory = LBFactory.latest_version;
        }
        LBFactory lbFactory;
        try {
            lbFactory = LBFactory.get(factory);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            sender.sendMessage("\u00a7cFactory version " + factory + " not found");
            return CommandResponse.SUCCESS;
        }

        int min = 0, max = 0;
        try {
            min = Integer.parseInt(args[2]);
            max = Integer.parseInt(args[3]);
        } catch (NumberFormatException ignored) {}

        if (min < 1 || max < min) {
            sender.sendMessage("\u00a7cPlease provide positive min <= max");
            return CommandResponse.SUCCESS;
        }

        long ms = System.currentTimeMillis();
        Config config = type.getNewConfigInstance();
        if (!config.hasResource()) config.write();
        config.reload();
        lbFactory.generate(config, type, min, max);
        config.save();
        type.load();
        sender.sendMessage("\u00a7aSuccessfully generated config for " + type.name()
                + " (in " + (System.currentTimeMillis() - ms) + " ms)");
        return CommandResponse.SUCCESS;

    }

    @Override
    public String[] commands() {
        return new String[] { "generate", "gen" };
    }

    @Override
    public MessagesManager.Message helpMessage() {
        return MessagesManager.Message.CMD_GENERATE;
    }

}
