package com.github.danirod12.luckyblock.command.piece;

//import com.github.danirod12.luckyblock.command.base.CommandResponse;
//import com.github.danirod12.luckyblock.command.base.LBPlayerCommand;
//import com.github.danirod12.luckyblock.customitem.BekkerItemStack;
//import com.github.danirod12.luckyblock.customitem.CustomItemFactory;
//import com.github.danirod12.luckyblock.util.Misc;
//import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
//import org.bukkit.entity.Player;

public class GetCustomItemCommand { //implements LBPlayerCommand {

//    @Override
//    public boolean permission() {
//        return true;
//    }
//
//    @Override
//    public CommandResponse execute(Player sender, String label, String[] args) {
//
//        if (args.length > 0) {
//
//            if (!args[0].contains("-"))
//                args[0] = "ntdluckyblock-" + args[0];
//
//            BekkerItemStack stack = CustomItemFactory.fetchCustomItem(args[0]);
//            if (stack == null) {
//                sender.sendMessage(Message.CI_NOT_FOUND.getAsString(true).replace("%identifier%", args[0]));
//                return CommandResponse.SUCCESS;
//            }
//            Misc.giveItemsOrDrop(sender, stack);
//            sender.sendMessage(Message.CMD_CI_RECEIVED.getAsString(true).replace("%item%",
//                                        stack.getIdentifier().getIdentifier()));
//            return CommandResponse.SUCCESS;
//
//        }
//        return CommandResponse.SEND_HELP;
//
//    }
//
//    @Override
//    public final String[] commands() {
//        return new String[]{"customitemget", "getcustomitem", "cig", "gci"};
//    }
//
//    @Override
//    public Message helpMessage() {
//        return Message.CMD_CUSTOMITEMGET;
//    }
}
