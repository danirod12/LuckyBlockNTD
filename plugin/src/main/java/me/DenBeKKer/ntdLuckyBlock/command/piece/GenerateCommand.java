package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class GenerateCommand extends LBCommand {

    private final LuckyBlockEngine engine;

    public GenerateCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_GENERATE, "generate", "gen");
        this.engine = engine;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(Message.CMD_GENERATE_DESCRIPTION.getAsString(true));
            return CommandResponse.SUCCESS;
        }

        Optional<LuckyBlockKey> key = engine.getLoaded(args[0]);
        if (!key.isPresent()) {
            sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[0]));
            return CommandResponse.SUCCESS;
        }

        int min = 0, max = 0;
        try {
            min = Integer.parseInt(args[1]);
            max = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored) {
        }

        if (min < 1 || max < min) {
            // TODO translate?
            sender.sendMessage("§cPlease provide positive min <= max");
            return CommandResponse.SUCCESS;
        }

        long ms = System.currentTimeMillis();
        LuckyBlock luckyBlock = engine.get(key.get()).orElseThrow(RuntimeException::new);
        luckyBlock.getItemsBag().reset();
        luckyBlock.getItemsBag().add(engine.getGenerationFactory().generateLuckyEntries(min, max));
        Config config = engine.getNewConfigInstance(luckyBlock.getKey().getKey());
        if (config.getFile().exists()) {
            config.load();
            engine.getGenerationFactory().saveLuckyEntries(config,
                    luckyBlock.getItemsBag().getEntries().toArray(new LuckyEntry[0]));
            config.save();
            // TODO translate?
            sender.sendMessage("§cNo configuration associated with LuckyBlock, changes will be unsaved");
        }
        // TODO translate?
        sender.sendMessage("§aSuccessfully generated config for " + key
                + " (in " + (System.currentTimeMillis() - ms) + " ms)");
        return CommandResponse.SUCCESS;
    }
}
