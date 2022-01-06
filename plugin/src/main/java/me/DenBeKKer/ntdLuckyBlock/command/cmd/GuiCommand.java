package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager.GuiType;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;

public class GuiCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public final String[] commands() {
		return new String[] { "gui" };
	}
	
	@Override
	public CommandResponce execute(Player player, String label, String[] args) {
		
		if(args.length > 0) {
			
			GuiType type = null;
			try {
				type = GuiType.valueOf(args[0].toUpperCase());
			} catch(Exception ingnored) {}
			if(type != null) {
				
				if(!Misc.hasPermission(player, "luckyblock.command.gui." + type.name())) return CommandResponce.MISSED_PERMISSION;
				LBMain.getInstance().gui_manager.open(type, player);
				return CommandResponce.SUCCESS;
				
			}
			
		}
		return CommandResponce.SEND_HELP;
		
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_GUI; }
	
}
