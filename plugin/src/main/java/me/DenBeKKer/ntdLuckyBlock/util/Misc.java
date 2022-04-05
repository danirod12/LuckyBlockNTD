package me.DenBeKKer.ntdLuckyBlock.util;

import org.bukkit.entity.Player;

import java.util.List;

public class Misc {
	
	/**
	 * 
	 * @param target Player to check permission
	 * @param node Permission node
	 * @return Does player have permission
	 * @throws UnsupportedOperationException Permission node or player is null
	 */
	public static boolean hasPermission(final Player target, String node) throws UnsupportedOperationException {
		
		if(node == null || target == null) throw new UnsupportedOperationException();
		
		node = node.toLowerCase();
		if(target.hasPermission(node) ||
				target.hasPermission(node + ".*")) return true;
		if(!node.contains(".")) return false;
		
		String permission = null;
		final String nodes[] = node.split("\\.");
		for(int i = 0; i < nodes.length - 1; ++i) {
			
			permission = permission == null ? nodes[i] : permission + "." + nodes[i];
			if(target.hasPermission(permission + ".*")) return true;
			
		}
		return false;
		
	}
	
	/**
	 * 
	 * Replace "&#rrggbbTest" to "§x§r§r§g§g§b§bTest"
	 * 
	 * @param origin String to be filled with colors
	 * @return Colored minecraft string
	 */
	public static String setColors(String origin) {
		return origin.replace("&", "§").replaceAll("§#([a-fA-F0-9]){1}"
				+ "([a-fA-F0-9]){1}([a-fA-F0-9]){1}([a-fA-F0-9]){1}([a-fA-F0-9]){1}([a-fA-F0-9]){1}", "§x§$1§$2§$3§$4§$5§$6");
	}
	
	/**
	 * 
	 * @param array Array to be stringed
	 * @param a Always insert brackets
	 * @return Stringed array
	 */
	public static String toString(List<?> array, boolean a) {
		
		if(array.size() == 0) return a ? "[]" : "";
		if(array.size() == 1) return (a ? "[]" : "") + array.get(0) + (a ? "[]" : "");
		
		String string = null;
		for(Object obj : array)
			string = string == null ? obj.toString() : string + ", " + obj.toString();
		return "[" + string + "]";
		
	}
	
}
