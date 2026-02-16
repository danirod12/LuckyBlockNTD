package me.DenBeKKer.ntdLuckyBlock.command.piece;

import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager.GuiType;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.entity.Player;

public class GuiCommand extends LBPlayerCommand {

    private final GuiManager guiManager;

    public GuiCommand(GuiManager guiManager) {
        super(true, Message.CMD_GUI, "gui");
        this.guiManager = guiManager;
    }

    @Override
    public CommandResponse execute(Player player, String label, String[] args) {
        if (args.length > 0) {
            GuiType type = GuiType.parseGuiType(args[0]);
            if (type != null) {
                if (!Misc.hasPermission(player, "luckyblock.command.gui." + type.name())) {
                    String permission;
                    switch (type) {
                        case GET: {
                            permission = "shop";
                            break;
                        }
                        case EDIT: {
                            permission = "editor";
                            break;
                        }
                        default: {
                            throw new RuntimeException();
                        }
                    }
                    if (!Misc.hasPermission(player, "luckyblock.command." + permission)) {
                        return CommandResponse.MISSED_PERMISSION;
                    }
                }
                this.guiManager.open(player, type);
                return CommandResponse.SUCCESS;
            }
        }
        return CommandResponse.SEND_HELP;
    }
}
