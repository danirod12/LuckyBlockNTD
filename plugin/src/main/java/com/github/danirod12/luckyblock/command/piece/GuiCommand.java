package com.github.danirod12.luckyblock.command.piece;

import com.github.danirod12.luckyblock.command.base.CommandResponse;
import com.github.danirod12.luckyblock.command.base.LBPlayerCommand;
import com.github.danirod12.luckyblock.util.Misc;
import com.github.danirod12.luckyblock.util.manager.GuiManager;
import com.github.danirod12.luckyblock.util.manager.GuiManager.GuiType;
import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
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
