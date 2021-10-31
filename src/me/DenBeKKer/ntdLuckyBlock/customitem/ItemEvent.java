package me.DenBeKKer.ntdLuckyBlock.customitem;

public final class ItemEvent<T> extends Object {
	
	public static final ItemEvent<InteractEvent> INTERACT = new ItemEvent<InteractEvent>();
	public static final ItemEvent<PlaceEvent> PLACE = new ItemEvent<PlaceEvent>();
	public static final ItemEvent<BreakEvent> BREAK = new ItemEvent<BreakEvent>();
	
}
