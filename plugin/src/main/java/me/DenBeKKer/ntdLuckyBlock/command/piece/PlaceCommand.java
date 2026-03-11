package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class PlaceCommand extends LBCommand {

    private final LuckyBlockEngine engine;

    public PlaceCommand(LuckyBlockEngine engine) {
        super(true, MessagesManager.Message.CMD_PLACE, "place", "setblock");
        this.engine = engine;
    }

    // [world] <x> <y> <z> <type>
    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        boolean notify = true;
        if (args.length > 0 && (args[args.length - 1].equalsIgnoreCase("-silent") ||
                args[args.length - 1].equalsIgnoreCase("-s"))) {
            notify = false;
            String[] argsCopy = args;
            args = new String[argsCopy.length - 1];
            System.arraycopy(argsCopy, 0, args, 0, args.length);
        }

        if (args.length >= 4 && args.length <= 5) {
            World world;
            Player player = null;
            Player target = null;
            if (args.length == 4) {
                if (sender instanceof Player) {
                    player = ((Player) sender);
                    target = player;
                    world = player.getWorld();
                } else {
                    sender.sendMessage("You should provide <world> for console invocation");
                    return CommandResponse.SUCCESS;
                }
            } else {
                if (args[0].startsWith("p:")) {
                    target = Bukkit.getPlayerExact(args[0].substring(2));
                    if (target == null) {
                        sender.sendMessage(MessagesManager.Message.CMD_PLAYER_NOT_FOUND.getAsString()
                                .replace("%player%", args[0].substring(2)));
                        return CommandResponse.SUCCESS;
                    }
                    world = target.getWorld();
                } else {
                    world = Bukkit.getWorld(args[0]);
                    if (world == null) {
                        sender.sendMessage(MessagesManager.Message.WORLD_NOT_FOUND.getAsString()
                                .replace("%world%", args[0]));
                        return CommandResponse.SUCCESS;
                    }
                }
            }

            double x, y, z;
            try {
                x = parseDouble(target, args[args.length - 4], Location::getX);
                y = parseDouble(target, args[args.length - 3], Location::getY);
                z = parseDouble(target, args[args.length - 2], Location::getZ);
            } catch (NumberFormatException exception) {
                return CommandResponse.SEND_HELP;
            }

            LuckyBlockKey key = this.engine.get(args[args.length - 1]);
            LuckyBlock instance = this.engine.get(key).orElse(null);
            if (instance == null) {
                sender.sendMessage(MessagesManager.Message.LB_NOT_FOUND.getAsString()
                        .replace("%world%", args[0]));
                return CommandResponse.SUCCESS;
            }
            if (player != null && !Misc.hasPermission(player,
                    "luckyblock.command.place." + key.toString().toLowerCase())) {
                sender.sendMessage(MessagesManager.Message.CMD_NO_PERM_TO_COLOR.getAsString());
                return CommandResponse.SUCCESS;
            }

            Location location = new Location(world, x, y, z);
            Block block = world.getBlockAt(location);
            engine.clearBlock(block, false);
            instance.placeBlock(block);

            if (notify) {
                sender.sendMessage(MessagesManager.Message.LB_PLACED.getAsString()
                        .replace("%type%", instance.getCustomName())
                        .replace("%location%", "(" + world.getName()
                                + ", x: " + location.getBlockX() + ", y: " + location.getBlockY()
                                + ", z: " + location.getBlockZ() + ")"));
            }
            return CommandResponse.SUCCESS;
        }
        return CommandResponse.SEND_HELP;
    }

    private double parseDouble(Player player, String value,
                               Function<Location, Double> mapper) throws NumberFormatException {
        // therefore here we can get an out of bounds, but passed "value" is guaranteed non-empty
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
}
