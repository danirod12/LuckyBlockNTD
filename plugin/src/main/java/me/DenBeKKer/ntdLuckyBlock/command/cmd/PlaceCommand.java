package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class PlaceCommand implements LBCommand {

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean permission() {
        return true;
    }

    // [world] <x> <y> <z> <type>
    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        if (args.length >= 4 && args.length <= 5) {
            World world;
            Player player = null;
            if (args.length == 4) {
                if (sender instanceof Player) {
                    player = ((Player) sender);
                    world = player.getWorld();
                } else {
                    sender.sendMessage("You should provide <world> for console invocation");
                    return CommandResponse.SUCCESS;
                }
            } else {
                world = Bukkit.getWorld(args[0]);
                if (world == null) {
                    sender.sendMessage(MessagesManager.Message.WORLD_NOT_FOUND.getAsString()
                            .replace("%world%", args[0]));
                    return CommandResponse.SUCCESS;
                }
            }

            double x, y, z;
            try {
                x = parseDouble(player, args[args.length - 4], Location::getX);
                y = parseDouble(player, args[args.length - 3], Location::getY);
                z = parseDouble(player, args[args.length - 2], Location::getZ);
            } catch (NumberFormatException exception) {
                return CommandResponse.SEND_HELP;
            }

            LuckyBlockType type = LuckyBlockType.parse(args[args.length - 1]);
            if (type == null || !type.isLoaded()) {
                sender.sendMessage(MessagesManager.Message.LB_NOT_FOUND.getAsString()
                        .replace("%world%", args[0]));
                return CommandResponse.SUCCESS;
            }
            if (player != null && !Misc.hasPermission(player,
                    "luckyblock.command.place." + type.name().toLowerCase())) {
                sender.sendMessage(MessagesManager.Message.CMD_NO_PERM_TO_COLOR.getAsString());
                return CommandResponse.SUCCESS;
            }

            Location location = new Location(world, x, y, z);
            try {
                LuckyBlockAPI.placeLuckyBlock(world.getBlockAt(location), type);
            } catch (LuckyBlockNotLoadedException exception) {
                throw new RuntimeException(exception);
            }
            sender.sendMessage(MessagesManager.Message.LB_PLACED.getAsString()
                    .replace("%type%", type.forceGet().getCustomName())
                    .replace("%location%", "(x: " + location.getBlockX()
                            + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ")"));
            return CommandResponse.SUCCESS;
        }
        return CommandResponse.SEND_HELP;
    }

    private double parseDouble(Player player, String value,
                               Function<Location, Double> mapper) throws NumberFormatException {
        if (value.toCharArray()[0] == '~') {
            if (player == null) {
                throw new NumberFormatException();
            }
            String substring = value.substring(1);
            return mapper.apply(player.getLocation()) + (substring.isEmpty() ? 0
                    : Double.parseDouble(value.substring(1)));
        }
        return Double.parseDouble(value);
    }

    @Override
    public final String[] commands() {
        return new String[]{"place", "setblock"};
    }

    @Override
    public MessagesManager.Message helpMessage() {
        return MessagesManager.Message.CMD_PLACE;
    }
}
