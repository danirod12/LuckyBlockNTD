package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class BekkerItemStack extends ItemStack {
	
	private final Identifier identifier;
	private final HashMap<ItemEvent<?>, Object> map;
	
	public BekkerItemStack(Identifier identifier, ItemStack stack, HashMap<ItemEvent<?>, Object> map) {
		super(identifier.apply(stack));
		this.identifier = identifier;
		this.map = map;
	}
	
	public void register() { CustomItemFactory.register(this); }
	
	@Override
	public boolean equals(Object object) {
		return identifier.equals(object);
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public void handleInteract(PlayerInteractEvent e) {
		LBMain.debug("Interact - " + identifier.toString());
		if(map.containsKey(ItemEvent.INTERACT))
			((InteractEvent)map.get(ItemEvent.INTERACT)).execute(e);
	}
	
	public void handlePlace(BlockPlaceEvent e) {
		LBMain.debug("Place - " + identifier.toString());
		if(map.containsKey(ItemEvent.PLACE))
			((PlaceEvent)map.get(ItemEvent.PLACE)).execute(e);
	}
	
	public HashMap<ItemEvent<?>, Object> getEvents() { return map; }
	
	public void handleBreak(BlockBreakEvent e) {
		LBMain.debug("Break - " + identifier.toString());
		if(map.containsKey(ItemEvent.BREAK))
			((BreakEvent)map.get(ItemEvent.BREAK)).execute(e);
	}
	
	public void handleHit(Entity damager, Entity victim, HitEvent.Type type) {
		LBMain.debug("Hit - " + identifier.toString());
		if(map.containsKey(ItemEvent.HIT))
			((HitEvent)map.get(ItemEvent.HIT)).execute(damager, victim, type);
	}
	
}
