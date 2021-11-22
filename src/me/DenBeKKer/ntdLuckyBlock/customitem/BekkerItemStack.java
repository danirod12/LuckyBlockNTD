package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class BekkerItemStack extends ItemStack {
	
	private final Identifier identifier;
	private final HashMap<ItemEvent<?>, Consumer<Event>> map;
	
	public BekkerItemStack(Identifier identifier, ItemStack stack, HashMap<ItemEvent<?>, Consumer<Event>> map) {
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
	
	public Set<ItemEvent<?>> getEvents() { return map.keySet(); }
	
	public void handle(Event event) {
		
		LBMain.debug(event.getClass().getSimpleName() + " - " + identifier.getIdentifier());
		for(Entry<ItemEvent<?>, Consumer<Event>> element : map.entrySet()) {
			
			if(element.getKey().getInstance().isAssignableFrom(event.getClass())) {
				element.getValue().accept(event);
				return;
			}
			
		}
		
	}
	
}
