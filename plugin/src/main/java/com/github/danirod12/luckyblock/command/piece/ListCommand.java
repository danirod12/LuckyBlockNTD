package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.model.LuckyBlock;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.model.LuckyBlockType;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ListCommand extends LBCommand {

    private final LuckyBlockEngine engine;

    public ListCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_LIST, "list", "luckyblocks", "status");
        this.engine = engine;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        List<LuckyBlockKey> list = new ArrayList<>(Arrays.asList(this.engine.getLoadedTypes()));
        for (LuckyBlockType internalValue : LuckyBlockType.values()) {
            if (list.stream().noneMatch(key -> key.getInternal() == internalValue)) {
                list.add(new LuckyBlockKey.NotLoadedLuckyBlockKey(internalValue.name()));
            }
        }
        list.sort(Comparator.comparingInt(key ->
                (key instanceof LuckyBlockKey.NotLoadedLuckyBlockKey ? 10 : 0)
                        + (key.isInternal() ? 1 : 0)));

        for (LuckyBlockKey key : list) {
            sendInfo(sender, key);
        }
        return CommandResponse.SUCCESS;
    }

    private void sendInfo(CommandSender sender, LuckyBlockKey key) {
        LuckyBlock block = this.engine.get(key).orElse(null);
        StringBuilder builder = new StringBuilder("§8 • §");
        builder.append(key.getColorData().asColorCode()).append(key);
        if (key.isInternal()) {
            builder.append(" §7[INTERNAL]");
        }
        if (block == null) {
            builder.append(" §cNot loaded");
        } else {
            builder.append(" §aEnabled §7(Entries: ").append(block.getItemsBag().size());
            builder.append(", Drops: ").append(block.getItemsBag().getTotalDropsAmount()).append(")");
        }
        sender.sendMessage(builder.toString());
    }
}
