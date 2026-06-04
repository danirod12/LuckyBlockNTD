package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.model.LuckyBlock;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.util.Config;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.engine.generator.AdvancedLootDatabase;
import com.github.danirod12.luckyblock.engine.generator.AdvancedLootGenerator;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import com.github.danirod12.luckyblock.util.random.WeightListAmount;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class GenerateCommand extends LBCommand {

    private final LuckyBlockEngine engine;
    private final AdvancedLootDatabase db;

    public GenerateCommand(LuckyBlockEngine engine, AdvancedLootDatabase db) {
        super(true, Message.CMD_GENERATE, "generate", "gen");
        this.engine = engine;
        this.db = db;
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

        int min, max, entriesCount;
        try {
            min = Integer.parseInt(args[1]);
            max = Integer.parseInt(args[2]);
            entriesCount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cArguments have to be numerical");
            return CommandResponse.SUCCESS;
        }

        if (min < 1 || max < min || entriesCount < 1) {
            sender.sendMessage("§cRule: min >= 1, max >= min, count >= 1");
            return CommandResponse.SUCCESS;
        }

        LuckyBlock luckyBlock = engine.get(key.get()).orElse(null);
        if (luckyBlock == null) {
            sender.sendMessage("§cNo luckyblock object found in memory.");
            return CommandResponse.SUCCESS;
        }

        luckyBlock.getItemsBag().clear();

        AdvancedLootGenerator generator = AdvancedLootGenerator.builder(engine, db)
                .minItems(min)
                .maxItems(max)
                .enableSchematics(true)
                .build();

        for (int i = 0; i < entriesCount; i++) {
            WeightListAmount<LuckyDrop> entry = generator.generate();
            luckyBlock.getItemsBag().add(entry);
        }

        try {
            Config config = engine.getNewConfigInstance(luckyBlock.getKey().getKey());

            if (config != null && config.getFile().exists()) {
                config.load();
                engine.getItemBagLoader().write(config, luckyBlock.getItemsBag());
                config.save();
            } else {
                sender.sendMessage("§cConfig file for " + key.get().getKey()
                        + " not found. Changes will be temporary.");
            }
        } catch (Exception e) {
            sender.sendMessage("§cFailed to save generated drops to config: " + e.getMessage());
            e.printStackTrace();
        }

        sender.sendMessage("§aSuccessfully generated and saved " + entriesCount
                + " entries for " + key.get().getKey());
        return CommandResponse.SUCCESS;
    }
}
