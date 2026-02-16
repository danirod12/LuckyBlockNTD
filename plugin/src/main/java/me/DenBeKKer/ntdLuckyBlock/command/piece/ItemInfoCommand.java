package me.DenBeKKer.ntdLuckyBlock.command.piece;

import com.google.gson.Gson;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.command.base.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.base.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.nms.ItemTag;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyEntry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ItemInfoCommand implements LBPlayerCommand {

    @Override
    public boolean permission() {
        return true;
    }

    @Override
    public CommandResponse execute(Player player, String label, String[] args) {

        final ItemStack item = LBMain.getInstance().materialFactory.getItemInMainHand(player);

        if (item == null) {
            player.sendMessage("\u00a7cTake item in hand for this command");
            return CommandResponse.SUCCESS;
        }

        final BekkerItemStack stack = CustomItemFactory.fetchCustomItem(item);
        if (stack != null) {
            player.sendMessage("\u00a7eCustom item. Identifier - \u00a76" + stack.getIdentifier().getIdentifier());
        } else {
            final LuckyBlockType old = LuckyBlockAPI.parseOldLuckyBlock(item);
            if (old != null) {
                player.sendMessage("\u00a7cOld unconverted LuckyBlock - \u00a7e" + old.name());
                return CommandResponse.SUCCESS;
            }

            final LuckyBlockType uuid = LuckyBlockAPI.parseLuckyBlock(item, false);
            final LuckyBlockType tag = LuckyBlockAPI.parseLuckyBlock(item);

            if (tag != null || uuid != null) {

                if (uuid != null) {
                    player.sendMessage("\u00a7cItem UUID \"\u00a7f" + Misc.getUUID(item)
                            + "\u00a7c\" matches with \u00a7" + uuid.getCustomName(true) + "\u00a7c luckyblock");
                }
                if (tag != null) {
                    player.sendMessage("\u00a7cItem TAG matches with \u00a7" + tag.getCustomName(true) + "\u00a7c luckyblock");
                }

                if ((uuid != null || !LBMain.getInstance().getConvertManager().isVerifyUUID())
                        && (tag != null || !LBMain.getInstance().getConvertManager().isVerifyTAG())) {
                    player.sendMessage("\u00a7cThis items matches with \u00a7" + uuid.getCustomName(true) + "\u00a7c by config settings");
                    if (!uuid.isLoaded()) {
                        player.sendMessage("\u00a7cLuckyBlock for \u00a7" + uuid.getCustomName(true) + " \u00a7cnot loaded");
                    } else {
                        LuckyBlock block = LuckyBlockType.map().get(uuid);
                        player.sendMessage("§8 > §fEconomy enabled - §e"
                                + block.getShopSetup().isEnabled() + (block.getShopSetup().isEnabled()
                                ? "§7 (Price: " + block.getShopSetup().getPrice() + ")" : ""));
                        player.sendMessage("§8 > §fCraft recipes - §e" + block.getRecipes().size());
                        player.sendMessage("§8 > §fCustom name - §e" + block.getCustomName());
                        List<LuckyEntry> entryList = block.getItemsBag().getEntries();
                        player.sendMessage("§8 > §fAvailable drops - §e" + entryList.size() + "/"
                                + entryList.stream().mapToLong(List::size).sum());
                    }
                }

            }
        }

        player.sendMessage("\u00a7cItem type - \u00a7e" + item.getType());

        boolean tag = false, write = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-tag"))
                tag = true;
            else if (arg.equalsIgnoreCase("-write"))
                write = true;
        }

        if (tag) {
            ItemTag adapter = LBMain.getInstance().getItemTagAdapter();
            String tagJson = new Gson().toJson(adapter.getTag(adapter.asNMSCopy(item)));
            player.sendMessage(tagJson);
            if (write) {

                File file = new File(LBMain.getInstance().getDataFolder(), "item-tags.txt");

                boolean appendWelcomeMessage = false;
                if (!file.exists()) {

                    // Should do nothing
                    LBMain.getInstance().getDataFolder().mkdirs();

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return CommandResponse.SUCCESS;
                    }
                    appendWelcomeMessage = true;

                }

                try (BufferedWriter buffer = new BufferedWriter(new FileWriter(file, true))) {
                    if (appendWelcomeMessage)
                        buffer.write("###   File for logging \"/ntdluckyblock iteminfo -tag -write\". Item tags in JSON   ###");
                    buffer.newLine();
                    buffer.newLine();
                    buffer.write(new SimpleDateFormat("dd-MM-yyyy hh:mm").format(new Date())
                            + " " + item.getType() + " (" + LBMain.getNMSVersion() + "):");
                    buffer.newLine();
                    buffer.write("ItemStack TAG: " + tagJson);
                    buffer.newLine();
                    buffer.write("ItemStack JSON: " + "Supported only in premium version");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }

        } else {
            player.sendMessage("\u00a77[\u00a7eLuckyBlock\u00a77] \u00a7aTip: \u00a7fTo see item tag, add \u00a7e-tag" +
                    "\u00a7f param. Also, you can add \u00a7e-write\u00a7f param to save output to " +
                    "\u00a7e../ntdLuckyBlock/item-tags.txt\u00a77 (If you want to copy it and use somewhere)");
        }
        return CommandResponse.SUCCESS;

    }

    @Override
    public final String[] commands() {
        return new String[]{"iteminfo", "ii"};
    }

    @Override
    public Message helpMessage() {
        return Message.CMD_ITEMINFO;
    }

}
