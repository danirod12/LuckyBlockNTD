package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.api.model.LuckyBlock;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// TODO drop if inventory is full
public class GiveCommand extends LBCommand {

    private final LuckyBlockEngine engine;

    public GiveCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_GIVE, "give");
        this.engine = engine;
    }

    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        if (args.length > 1) {
            List<Player> targets;
            if (args[0].equalsIgnoreCase("all")) {
                targets = new ArrayList<>(Bukkit.getOnlinePlayers());
            } else if (args[0].equalsIgnoreCase("current")
                    || args[0].equalsIgnoreCase("currentWorld")) {
                if (sender instanceof Player) {
                    targets = ((Player) sender).getWorld().getPlayers();
                } else {
                    sender.sendMessage("§cSpecify world name");
                    return CommandResponse.SUCCESS;
                }
            } else {
                Player player = Bukkit.getPlayerExact(args[0]);
                if (player == null) {
                    World world = Bukkit.getWorld(args[0]);
                    if (world == null) {
                        sender.sendMessage(Message.CMD_PLAYER_NOT_FOUND.getAsString()
                                .replace("%player%", args[0]));
                        return CommandResponse.SUCCESS;
                    }
                    targets = world.getPlayers();
                } else {
                    targets = Collections.singletonList(player);
                }
            }

            LuckyBlockKey key;
            if (args[1].equalsIgnoreCase("random")) {
                LuckyBlockKey[] array = Arrays.stream(engine.getLoadedTypes())
                        .filter(k -> this.hasPermission(sender, k)).toArray(LuckyBlockKey[]::new);
                key = array.length == 0 ? null : array[ThreadLocalRandom.current().nextInt(array.length)];
            } else {
                key = engine.getLoaded(args[0]).orElse(null);
            }

            if (key == null) {
                sender.sendMessage(Message.CMD_LB_NOT_FOUND.getAsString().replace("%lb%", args[0]));
                return CommandResponse.SUCCESS;
            }
            LuckyBlock block = engine.get(key).orElseThrow(RuntimeException::new);
            if (!hasPermission(sender, key)) {
                sender.sendMessage(Message.CMD_NO_PERM_TO_COLOR.getAsString()
                        .replace("%lb%", block.getCustomName()));
                return CommandResponse.SUCCESS;
            }

            int amount = Misc.parseInteger(args[2], 1);
            ItemStack stack = block.getItem(amount);
            for (Player target : targets) {
                Misc.giveItemsOrDrop(target, stack);
            }
            sender.sendMessage(Message.CMD_LB_GIVE.getAsString().replace("%lb%", block.getCustomName())
                    .replace("%amount%", String.valueOf(amount)));
            return CommandResponse.SUCCESS;
        }
        return CommandResponse.SEND_HELP;
    }

    private boolean hasPermission(CommandSender sender, LuckyBlockKey key) {
        if (sender instanceof Player) {
            if (this.engine.getConfigHolder().getCommandPermissionsForEach) {
                return sender.hasPermission("luckyblock.command.get." + key);
            } else {
                return sender.hasPermission("luckyblock.command.get");
            }
        }
        return true;
    }
}
