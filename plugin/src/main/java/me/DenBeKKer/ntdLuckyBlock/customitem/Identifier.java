package me.DenBeKKer.ntdLuckyBlock.customitem;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.nms.ItemTag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

public class Identifier {

	private static final Pattern pattern = Pattern.compile("[a-z][a-z0-9_]{2,}[a-z0-9]");
	
	private final String identifier, tag_name;
	
	/**
	 * Custom tags with plugin
	 */
	public Identifier(Plugin plugin, String tag_name, String identifier) {
		
		if(!pattern.matcher(identifier).matches())
			throw new UnsupportedOperationException("Identifier must be \"" + pattern.pattern() + "\"");
		
		this.identifier = plugin.getName().toLowerCase() + "-" + identifier;
		this.tag_name = tag_name;
		
	}
	
	/**
	 * Custom items
	 */
	public Identifier(Plugin plugin, String identifier) {
		this(plugin, CustomItemFactory.TAG_IDENTIFIER_NAME, identifier);
	}
	
	/**
	 * Custom tags without plugin
	 */
	public Identifier(String tag_name, String identifier) {
		this.identifier = identifier;
		this.tag_name = tag_name;
	}
	
	public ItemStack apply(ItemStack origin) {
		ItemTag adapter = LBMain.getItemTagAdapter();
		Object nmsItem = adapter.asNMSCopy(origin);
		Object tag = adapter.getTag(nmsItem);
		if(tag == null) tag = adapter.newTag();
		adapter.setTagString(tag, tag_name, identifier);
		adapter.setTag(nmsItem, tag);
		return adapter.asBukkitCopy(nmsItem);
	}
	
	@Deprecated
	@Override
	public String toString() {
		return identifier;
	}
	
	@Deprecated
	public boolean isItem(ItemStack item) {
		return compare(item);
	}
	
	@Override
	public boolean equals(Object object) {
		
		if(object == null) return false;
		if(object instanceof String) return compare((String) object);
		if(object instanceof Identifier) return compare((Identifier) object);
		if(object instanceof ItemStack) return compare((ItemStack) object);
		else return false;
		
	}
	
	public String getTagName() { return tag_name; }
	public String getIdentifier() { return identifier; }
	
	public boolean compare(ItemStack stack) {
		return CustomItemFactory.compare(stack, tag_name, identifier);
	}
	
	public boolean compare(Identifier identifier) {
		return identifier.identifier.equalsIgnoreCase(this.identifier);
	}
	
	public boolean compare(String string) {
		return string.equalsIgnoreCase(identifier);
	}

}
