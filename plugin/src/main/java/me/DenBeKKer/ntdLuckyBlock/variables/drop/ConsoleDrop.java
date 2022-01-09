package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ConsoleDrop implements LuckyDrop {
	
	@SerializedName(value = "command")
	private final String cmd;
	
	/**
	 * @param command - Command that will be executed
	 */
	public ConsoleDrop(String command) {
		this.cmd = command;
	}
	
	private String getLocation(Player target) {
		Location location = target.getLocation();
		return location.getX() + " " + location.getY() + " " + location.getZ();
	}
	
	@Override
	public void execute(LBMain.LuckyBlockType related, Block b, Player player) {

		String cmd = this.cmd;
		if(cmd.contains("%player%") || cmd.contains("%world%") || cmd.contains("%location%")) {
			if(player == null) return;
			cmd = cmd.replace("%player%", player.getName())
					.replace("%world%", b.getWorld().getName()).replace("%location%", getLocation(player));
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		
	}

	public String getCommand() { return cmd; }
	
}
