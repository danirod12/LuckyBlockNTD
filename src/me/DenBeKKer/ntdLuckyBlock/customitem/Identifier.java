package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.DenBeKKer.ntdLuckyBlock.LBMain;

public class Identifier {
	
	private static Class<?> CraftItemStack, NBTTagCompound;
	
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
	
	private final String identifier;
	
	public Identifier(Plugin plugin, String identifier) {
		this.identifier = plugin.getName().toLowerCase() + "-" + identifier;
	}
	
	public ItemStack apply(ItemStack origin) {
		Object nmsItem = asNMSCopy(origin);
		Object tag = getTag(nmsItem);
		if(tag == null) tag = newTag();
		setTagString(tag, CustomItemFactory.TAG_IDENTIFIER_NAME, identifier);
		setTag(nmsItem, tag);
		return asBukkitCopy(nmsItem);
	}
	
	@Override
	public String toString() {
		return identifier;
	}
	
	public boolean isItem(ItemStack item) {
		return equals((Object) item);
	}
	
	@Override
	public boolean equals(Object object) {
		
		if(object == null) return false;
		
		if(object instanceof String)
			return ((String)object).equalsIgnoreCase(identifier);
		if(object instanceof Identifier)
			return ((Identifier)object).identifier.equalsIgnoreCase(identifier);
		if(object instanceof ItemStack)
			return identifier.equalsIgnoreCase(notNull(getTagString(getTag(asNMSCopy((ItemStack) object)), CustomItemFactory.TAG_IDENTIFIER_NAME)));
		else return false;
		
	}
	
	private String notNull(String string) { return string == null ? "" : string; }
	
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
