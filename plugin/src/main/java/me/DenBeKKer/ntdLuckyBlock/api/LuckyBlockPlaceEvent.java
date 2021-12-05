package me.DenBeKKer.ntdLuckyBlock.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;

public class LuckyBlockPlaceEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	private Block block;
	private Player player;
	private LuckyBlockType luckyblock;
	
	public LuckyBlockPlaceEvent(Block b, Player p, LuckyBlockType type) {
		block = b;
		player = p;
		luckyblock = type;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public LuckyBlockType getLuckyBlockType() {
		return luckyblock;
	}
	
	public LuckyBlock getLuckyBlock() throws LuckyBlockNotLoadedException {
		if(luckyblock.get() == null)
			throw new LuckyBlockNotLoadedException(luckyblock);
		return luckyblock.get();
	}
	
	private boolean c = false;
	
	@Override
	public boolean isCancelled() { return c; }
	 
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
