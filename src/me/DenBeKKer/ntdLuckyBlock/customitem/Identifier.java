package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class Identifier {
	
	private static Class<?> CraftItemStack, NBTTagCompound;
	private static final Pattern pattern = Pattern.compile("[a-z]{1}[a-z0-9_]{2,}[a-z0-9]{1}");
	
	static {
		
		try {
			CraftItemStack = Class.forName("org.bukkit.craftbukkit." + LBMain.getNMSVersion() + ".inventory.CraftItemStack");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Bukkit.shutdown();
		}
		
		try {
			NBTTagCompound = Class.forName("net.minecraft.server." + LBMain.getNMSVersion() + ".NBTTagCompound");
		} catch(Exception ex) {
			try {
				NBTTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
			} catch(Exception ex2) {
				ex.printStackTrace();
				ex2.printStackTrace();
			}
		}
		
	}
	
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
		Object nmsItem = asNMSCopy(origin);
		Object tag = getTag(nmsItem);
		if(tag == null) tag = newTag();
		setTagString(tag, tag_name, identifier);
		setTag(nmsItem, tag);
		return asBukkitCopy(nmsItem);
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
	
	public static ItemStack asBukkitCopy(Object nmsItem) {
		try {
			return (ItemStack) CraftItemStack.getMethod("asBukkitCopy", nmsItem.getClass()).invoke(null, nmsItem);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static Object newTag() {
		try {
			return NBTTagCompound.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object asNMSCopy(ItemStack origin) {
		try {
			return CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, origin);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getTagString(Object tag, String element) {
		try {
			return (String) tag.getClass().getMethod("getString", String.class).invoke(tag, element);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setTagString(Object tag, String element, String value) {
		try {
			tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, element, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static Object getTag(Object nmsItem) {
		try {
			return nmsItem.getClass().getMethod("getTag").invoke(nmsItem);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setTag(Object nmsItem, Object newTag) {
		try {
			nmsItem.getClass().getMethod("setTag", newTag.getClass()).invoke(nmsItem, newTag);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
}
