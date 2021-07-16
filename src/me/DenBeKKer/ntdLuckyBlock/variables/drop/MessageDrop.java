package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class MessageDrop implements LuckyDrop {
	
	@SerializedName(value = "message")
	private String cmd;
	
	public MessageDrop(String message) {
		this.cmd = message.replace("&", "\u00a7");
	}
	
	@Override
	public void execute(Block b, Player target) {
		target.sendMessage(cmd.replace("%player%", target.getName()));
	}
	
	@Override
	public void execute(Block b) { return; }
	
}
