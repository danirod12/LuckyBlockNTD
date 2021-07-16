package me.DenBeKKer.ntdLuckyBlock.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;

public class LuckyBlockBreakEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	private Block block;
	private Player player;
	private LuckyBlock luckyblock;
	private boolean targetable = true, ignore = false, c = false;
	
	public LuckyBlockBreakEvent(Block b, Player p, LuckyBlock lb) {
		block = b;
		player = p;
		luckyblock = lb;
	}
	
	public LuckyBlockBreakEvent(Block b, LuckyBlock lb) {
		block = b;
		targetable = false;
		luckyblock = lb;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public boolean isTargetable() {
		return targetable;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public LuckyBlock getLuckyBlock() {
		return luckyblock;
	}
	
	public void setIgnoreCancelled() { ignore = true; }
	
	@Override
	public boolean isCancelled() { return ignore || c; }
	 
	@Override
	public void setCancelled(boolean c) { this.c = c; }
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}
