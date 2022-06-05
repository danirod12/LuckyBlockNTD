package me.DenBeKKer.ntdLuckyBlock.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface LBPlayerCommand extends LBCommand {

    default boolean onlyPlayer() {
        return true;
    }

    default CommandResponse execute(CommandSender sender, String label, String args[]) {
        return execute((Player) sender, label, args);
    }

    CommandResponse execute(Player player, String label, String[] args);

}
