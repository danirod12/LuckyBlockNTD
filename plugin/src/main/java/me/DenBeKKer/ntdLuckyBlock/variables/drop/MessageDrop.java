package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MessageDrop implements LuckyDrop {
	
	@SerializedName(value = "message")
	private final String cmd;
	
	/**
	 * 
	 * @param message - Message will be sended
	 */
	public MessageDrop(String message) {
		this.cmd = message.replace("&", "\u00a7");
	}
	
	@Override
	public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
		if(target == null && cmd.contains("%player%")) return;
		target.sendMessage(cmd.replace("%player%", target.getName()).replace("%world%", b.getWorld().getName()));
	}

	public String getMessage() {
		return cmd;
	}

}
