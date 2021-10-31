package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.HashMap;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BekkerItemStack extends ItemStack {
	
	private final Identifier identifier;
	private final HashMap<ItemEvent<?>, Object> map;
	
	public BekkerItemStack(Identifier identifier, ItemStack stack, HashMap<ItemEvent<?>, Object> map) {
		super(identifier.apply(stack));
		this.identifier = identifier;
		this.map = map;
	}
	
	public void register() { CustomItemFactory.register(this); }
	
	// TODO
	
	@Override
	public boolean equals(Object object) {
		return identifier.equals(object);
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public void handleInteract(PlayerInteractEvent e) {
		if(map.containsKey(ItemEvent.INTERACT))
			((InteractEvent)map.get(ItemEvent.INTERACT)).execute(e);
	}
	
	public void handlePlace(BlockPlaceEvent e) {
		if(map.containsKey(ItemEvent.PLACE))
			((PlaceEvent)map.get(ItemEvent.PLACE)).execute(e);
	}
	
	public HashMap<ItemEvent<?>, Object> getEvents() { return map; }
	
	public void handleBreak(BlockBreakEvent e) {
		if(map.containsKey(ItemEvent.BREAK))
			((BreakEvent)map.get(ItemEvent.BREAK)).execute(e);
	}
	
}
