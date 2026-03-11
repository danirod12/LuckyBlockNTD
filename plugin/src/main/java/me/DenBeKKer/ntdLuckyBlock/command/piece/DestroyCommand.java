package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBCommand;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DestroyCommand extends LBCommand {

    private LuckyBlockEngine engine;

    public DestroyCommand(LuckyBlockEngine engine) {
        super(true, Message.CMD_DESTROY, "destroy", "kill");
        this.engine = engine;
    }

    // lb destroy <chunk|radius> [-all]
    // lb destroy <xyz> [xyz] [world] [-all]
    @Override
    public CommandResponse execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            return CommandResponse.SEND_HELP;
        } else if (!(sender instanceof Player) && (args[0].equalsIgnoreCase("chunk") || args.length < 3)) {
            sender.sendMessage("§cThis command's subcommand is only available for players. Use /"
                    + label + " <xyz> [xyz] <world> [-all]");
            return CommandResponse.SUCCESS;
        }

        // Notification
        boolean notify = true;
        if (args[args.length - 1].equalsIgnoreCase("-silent")
                || args[args.length - 1].equalsIgnoreCase("-s")) {
            notify = false;
            String[] argsCopy = args;
            args = new String[argsCopy.length - 1];
            System.arraycopy(argsCopy, 0, args, 0, args.length);
        }

        // Check if we should destroy all (latest argument is "-all" ignore case)
        boolean destroyAll = args[args.length - 1].equalsIgnoreCase("-all");
        if (destroyAll) {
            String[] argsNoLast = new String[args.length - 1];
            System.arraycopy(args, 0, argsNoLast, 0, argsNoLast.length);
            args = argsNoLast;
        }

        List<Entity> entities = new ArrayList<>();
        // if argument is chunk then destroy all entities in the chunk
        // else if arguments length is less than 3 then destroy all entities in the radius
        // else load two positions from the arguments and destroy all entities in the area
        // if only one position is given then threat the second position as the first position
        if (args[0].equalsIgnoreCase("chunk")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command's subcommand is only available for players");
                return CommandResponse.SUCCESS;
            }
            entities.addAll(Arrays.asList(((Player) sender).getLocation().getChunk().getEntities()));
        } else if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command's subcommand is only available for players");
                return CommandResponse.SUCCESS;
            }
            try {
                int radius = Integer.parseInt(args[0]);
                entities.addAll(((Player) sender).getWorld().getNearbyEntities(((Player) sender).getLocation()
                        .clone().add(0, -1.2, 0), radius, radius, radius));
            } catch (NumberFormatException exception) {
                return CommandResponse.SEND_HELP;
            }
        } else {
            try {
                double x1 = Double.parseDouble(args[0]);
                double y1 = Double.parseDouble(args[1]);
                double z1 = Double.parseDouble(args[2]);

                double x2 = x1;
                double y2 = y1;
                double z2 = z1;
                if (args.length > 5) {
                    x2 = Double.parseDouble(args[3]);
                    y2 = Double.parseDouble(args[4]);
                    z2 = Double.parseDouble(args[5]);
                }

                // set x1 as min of x1 x2, set x2 as max of x1 x2 and so on, change values if x1>x2
                if (x1 > x2) {
                    double temp = x1;
                    x1 = x2;
                    x2 = temp;
                }
                if (y1 > y2) {
                    double temp = y1;
                    y1 = y2;
                    y2 = temp;
                }
                if (z1 > z2) {
                    double temp = z1;
                    z1 = z2;
                    z2 = temp;
                }

                y1 -= 1.2;
                x2++;
                y2 -= 1.2;
                z2++;

                World world;
                if (args.length > 6 || args.length > 3 && args.length < 6) {
                    world = Bukkit.getWorld(args[args.length > 6 ? 6 : 3]);
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cYou should provide a world");
                        return CommandResponse.SUCCESS;
                    }
                    world = ((Player) sender).getWorld();
                }
                if (world == null) {
                    sender.sendMessage(Message.WORLD_NOT_FOUND.getAsString(true)
                            .replace("%world%", args[6]));
                    return CommandResponse.SUCCESS;
                }

                for (Entity entity : world.getEntities()) {
                    if (entity.getLocation().getX() >= x1 && entity.getLocation().getX() <= x2
                            && entity.getLocation().getY() >= y1 && entity.getLocation().getY() <= y2
                            && entity.getLocation().getZ() >= z1 && entity.getLocation().getZ() <= z2) {
                        entities.add(entity);
                    }
                }
            } catch (NumberFormatException exception) {
                return CommandResponse.SEND_HELP;
            }
        }

        int notRemoved = engine.destroyEntities(destroyAll, destroyAll, entities.toArray(new Entity[0])).size();
        if (notify) {
            sender.sendMessage(Message.CMD_DESTROYED_LB.getAsString(true)
                    .replace("%amount%", String.valueOf(entities.size() - notRemoved)));
        }
        return CommandResponse.SUCCESS;
    }
}
