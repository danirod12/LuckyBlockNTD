package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.command.CommandSender;

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
        List<LuckyBlockKey> list = Arrays.asList(this.engine.getLoadedTypes());
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
