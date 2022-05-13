package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponse;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.GuiManager.GuiType;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import org.bukkit.entity.Player;

public class GuiCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public final String[] commands() {
		return new String[] { "gui" };
	}
	
	@Override
	public CommandResponse execute(Player player, String label, String[] args) {
		
		if(args.length > 0) {
			
			GuiType type = GuiType.parseGuiType(args[0]);
			if(type != null) {
				
				if(!Misc.hasPermission(player, "luckyblock.command.gui." + type.name())) return CommandResponse.MISSED_PERMISSION;
				LBMain.getInstance().gui_manager.open(player, type);
				return CommandResponse.SUCCESS;
				
			}
			
		}
		return CommandResponse.SEND_HELP;
		
	}
	
	@Override
	public Message helpMessage() { return Message.CMD_GUI; }
	
}
