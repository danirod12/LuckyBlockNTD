package com.github.danirod12.luckyblock.command.piece;

//import com.github.danirod12.luckyblock.LBMain;
//import com.github.danirod12.luckyblock.command.base.CommandResponse;
//import com.github.danirod12.luckyblock.command.base.LBCommand;
//import com.github.danirod12.luckyblock.customitem.BekkerItemStack;
//import com.github.danirod12.luckyblock.customitem.CustomItemFactory;
//import com.github.danirod12.luckyblock.util.manager.MessagesManager.Message;
//import org.bukkit.command.CommandSender;
//
//import java.util.Collection;

public class ListCustomItemsCommand { //implements LBCommand {

//    @Override
//    public boolean onlyPlayer() {
//        return false;
//    }
//
//    @Override
//    public boolean permission() {
//        return true;
//    }
//
//    @Override
//    public CommandResponse execute(CommandSender sender, String label, String[] args) {
//
//        sender.sendMessage(Message.CI_LIST.getAsString(true));
//
//        Collection<BekkerItemStack> collection = CustomItemFactory.copy();
//        if (collection.size() == 0)
//            sender.sendMessage("\u00a77 >>\u00a7c List is empty");
//        else for (BekkerItemStack stack : collection) {
//            final String plugin = stack.getIdentifier().getIdentifier().split("-")[0];
//            sender.sendMessage("\u00a78 > \u00a7e" + stack.getIdentifier().getIdentifier()
//                    + " \u00a77(" + (plugin.equalsIgnoreCase(LBMain.getInstance().getName())
//                    ? "\u00a76" : "\u00a7e") + plugin + "\u00a77)");
//        }
//        return CommandResponse.SUCCESS;
//
//    }
//
//    @Override
//    public final String[] commands() {
//        return new String[]{"customitemslist", "listcustomitems", "lci", "cil"};
//    }
//
//    @Override
//    public Message helpMessage() {
//        return Message.CMD_CUSTOMITEMSLIST;
//    }
}
