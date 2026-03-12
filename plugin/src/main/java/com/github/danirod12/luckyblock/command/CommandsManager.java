package com.github.danirod12.luckyblock.command;

import com.github.danirod12.luckyblock.LBMain;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.provider.LBMainProvider;
import com.github.danirod12.luckyblock.command.base.BaseAlias;
import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBCommand;
import com.github.danirod12.luckyblock.command.piece.*;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.stream.Collectors;

public class CommandsManager implements CommandExecutor, /* TODO TabCompleter, */ Listener {

    private final LuckyBlockEngine engine;
    private final List<LBCommand> commands = new ArrayList<>();
    private final List<BaseAlias> aliases = new ArrayList<>();

    public CommandsManager(LuckyBlockEngine engine, LBMainProvider provider) {
        this.engine = engine;

        // Basic
        register(new GetCommand(engine));
        register(new GiveCommand(engine));
        register(new PlaceCommand(engine));
        register(new GuiCommand(((LBMain) provider).getGuiManager())); // TODO rework

        // System
        register(new SupportCommand(engine.getLogChannel()));
        register(new UpdatesCommand(provider.getSpigotUpdater()));
        register(new ReloadCommand(engine));
        register(new DestroyCommand(engine));

        // Items Management
        register(new ListCommand(engine));
//        register(new ListCustomItemsCommand());
//        register(new GetCustomItemCommand());
//        register(new ItemInfoCommand());

        // Other
        register(new VersionCommand(provider.getSpigotUpdater(), engine.getVersionControl().getNmsVersion()));
        register(new ConvertCommand(engine.getConvertFactory()));
        register(new GenerateCommand(engine));

        // Redirections
        register(BaseAlias.build("shop", "gui get"));
        register(BaseAlias.build("editor", "gui edit"));

        this.engine.getLogChannel().debug("Loaded " + commands.size()
                + " subcommands and " + aliases.size() + " aliases");
    }

    public void register(LBCommand command) {
        this.commands.add(command);
    }

    public void register(BaseAlias alias) {
        this.aliases.add(alias);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !engine.getConfigHolder().reduceAuthorInfo) {
            sender.sendMessage("§7[§eLuckyBlock§7] §fRunning§a ntdLuckyBlock v" + LuckyBlockAPI.getVersion()
                    + " " + (LuckyBlockAPI.getVersionType().getColoredSimpleName()) + " §fby§a danirod12");
        }

        String redirect = this.aliases.stream().map(alias -> alias.getRedirection(args)).filter(Optional::isPresent)
                .findFirst().orElse(Optional.empty()).orElse(null);
        if (redirect != null) {
            Bukkit.dispatchCommand(sender, label + " " + redirect);
            return true;
        }

        sendHelp:
        if (args.length > 0) {
            LBCommand command = fetchCommand(args[0]);
            if (command == null) {
                break sendHelp;
            }

            if (sender instanceof Player) {
                if (command.isPermission() && !Misc.hasPermission((Player) sender,
                        "luckyblock.command." + command.getCommands()[0].toLowerCase())) {
                    sender.sendMessage(Message.CMD_NO_PERM.getAsString(true));
                    return true;
                }
            } else {
                if (command.isOnlyPlayer()) {
                    sender.sendMessage("Sorry, but command `/" + label + " " + args[0] + "` is only for players");
                    return true;
                }
            }

            try {
                CommandResponse response = command.execute(sender, label,
                        args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
                if (response == CommandResponse.SUCCESS) {
                    return true;
                }
                if (response == CommandResponse.MISSED_PERMISSION) {
                    sender.sendMessage(Message.CMD_NO_PERM.getAsString(true));
                    return true;
                }
            } catch (Throwable th) {
                th.printStackTrace();
                sender.sendMessage("§cAn exception occurred while performing command /" + label + " " + args[0]);
                sender.sendMessage("§8 - §fIf you are a player - §aReport this to staff");
                sender.sendMessage("§8 - §fIf you are a staff - Check console & report it to danirod12");
                return true;
            }
        }

        List<String> messages = commands.stream().filter(command -> {
            if (sender instanceof Player) {
                return !(command.isPermission() && !Misc.hasPermission((Player) sender,
                        "luckyblock.command." + command.getCommands()[0]));
            } else {
                return !command.isOnlyPlayer();
            }
        }).map(LBCommand::getHelpMessage).filter(Objects::nonNull).map(msg -> msg.getAsString(true)
                .replace("%label%", label)).collect(Collectors.toList());
        if (messages.isEmpty()) {
            return true;
        }
        sender.sendMessage(Message.CMD_HELP.getAsString(true));
        messages.forEach(sender::sendMessage);
        return true;
    }

    private LBCommand fetchCommand(String string) {
        for (LBCommand command : commands) {
            for (String cmd : command.getCommands()) {
                if (cmd.equalsIgnoreCase(string)) {
                    return command;
                }
            }
        }
        return null;
    }

    // TODO forward down to the commands
//    @Override
//    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
//        if (args.length > 1) {
//            if (sender instanceof Player && args[0].equalsIgnoreCase("get") && (args.length < 3)) {
//                return Arrays.stream(engine.getLoadedTypes()).map(LuckyBlockKey::getKey)
//                        .filter(n -> Misc.hasPermission((Player) sender, "luckyblock.command.get." + n))
//                        .filter(n -> n.startsWith(args[1].toUpperCase()))
//                        .collect(Collectors.toList());
//            }
//            if (args[0].equalsIgnoreCase("give") && (!(sender instanceof Player)
//                    || Misc.hasPermission((Player) sender, "luckyblock.command.give"))) {
//                if (args.length == 2) {
//                    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
//                            .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
//                            .collect(Collectors.toList());
//                }
//                if (args.length == 3) {
//                    return Arrays.stream(engine.getLoadedTypes()).map(LuckyBlockKey::getKey)
//                            .filter(n -> !(sender instanceof Player) ||
//                                    Misc.hasPermission((Player) sender, "luckyblock.command.give." + n))
//                            .filter(n -> n.startsWith(args[2].toUpperCase()))
//                            .collect(Collectors.toList());
//                }
//            }
//            if (args[0].equalsIgnoreCase("place") || args[0].equalsIgnoreCase("setblock")) {
//                if (!sender.hasPermission("luckyblock.command.place"))
//                    return new ArrayList<>();
//
//                if (args.length == 2) {
//                    // java.lang.NoClassDefFoundError: org/bukkit/generator/WorldInfo on lambda (World::getName)
//                    List<String> list = Bukkit.getWorlds().stream().map(WorldInfo::getName)
//                            .collect(Collectors.toCollection(ArrayList::new));
//                    list.addAll(Bukkit.getOnlinePlayers().stream().map(p -> "p:" + p.getName())
//                            .collect(Collectors.toList()));
//                    list = list.stream()
//                            .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
//                            .collect(Collectors.toList());
//                    if (sender instanceof Player) {
//                        list.add("~");
//                    }
//                    return list;
//                } else {
//                    boolean suggestLuckyBlocks = false;
//                    if (!args[1].replaceAll("[~0-9]", "").isEmpty()) {
//                        if (args[1].length() > 1 && Bukkit.getPlayerExact(args[1].substring(2)) != null) {
//                            // player
//                            if (args.length < 6) {
//                                return Collections.singletonList("~");
//                            } else if (args.length == 6) {
//                                suggestLuckyBlocks = true;
//                            }
//                        } else {
//                            // world
//                            suggestLuckyBlocks = args.length == 6;
//                        }
//                    } else {
//                        // location
//                        if (args.length < 5) {
//                            return Collections.singletonList("~");
//                        } else if (args.length == 5) {
//                            suggestLuckyBlocks = true;
//                        }
//                    }
//                    if (suggestLuckyBlocks) {
//                        List<String> list = LuckyBlockType.enabled().stream().map(Enum::name)
//                                .filter(n -> n.startsWith(args[args.length - 1].toUpperCase()))
//                                .filter(n -> !(sender instanceof Player) || Misc.hasPermission((Player) sender,
//                                        "luckyblock.command.place." + n))
//                                .collect(Collectors.toCollection(ArrayList::new));
//                        list.add("RANDOM");
//                        return list;
//                    }
//                }
//                return new ArrayList<>();
//            }
//            if ((args[0].equalsIgnoreCase("customitemget") ||
//                    args[0].equalsIgnoreCase("getcustomitem") ||
//                    args[0].equalsIgnoreCase("cig") ||
//                    args[0].equalsIgnoreCase("gci")) && (!(sender instanceof Player)
//                    || Misc.hasPermission((Player) sender, "luckyblock.command.customitemget"))) {
//                if (args.length == 2) {
//                    return CustomItemFactory.copy().stream().map(n -> n.getIdentifier().getIdentifier())
//                            .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()) ||
//                                    !args[1].contains("-") && n.split("-")[1].startsWith(args[1].toLowerCase()))
//                            .collect(Collectors.toList());
//                }
//            }
//            return new ArrayList<>();
//        }
//
//        return commands.stream().filter(command -> {
//            if (sender instanceof Player) {
//                return !(command.isPermission() && !Misc.hasPermission((Player) sender,
//                        "luckyblock.command." + command.getCommands()[0]));
//            } else {
//                return !command.isOnlyPlayer();
//            }
//        }).map(n -> n.getCommands()[0]).filter(n -> n.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
//    }
}
