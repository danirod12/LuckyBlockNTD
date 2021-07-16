package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class CommandDrop implements LuckyDrop {
	
	@SerializedName(value = "command")
	private String cmd;
	
	public CommandDrop(String command) {
		this.cmd = command;
	}
	
	@Override
	public void execute(Block b, Player target) {
		Bukkit.dispatchCommand(target, cmd.replace("%player%", target.getName()));
	}
	
	@Override
	public void execute(Block b) { return; }
	
}
