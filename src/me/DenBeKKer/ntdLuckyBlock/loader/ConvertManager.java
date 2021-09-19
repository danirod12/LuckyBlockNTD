package me.DenBeKKer.ntdLuckyBlock.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import me.DenBeKKer.ntdLuckyBlock.util.Config;

public class ConvertManager {
	
	private static HashMap<Config, Collection<String>> map = new HashMap<>();
	
	public static void add(Config loaded, String path) {
		
		Collection<String> collection = map.containsKey(loaded) ? map.get(loaded) : new ArrayList<>();
		collection.add(path);
		map.put(loaded, collection);
		
	}
	
	public static int getRequests() {
		int i = 0;
		for(Collection<String> collection : map.values())
			i += collection.size();
		return i;
	}
	
	public static HashMap<Config, Collection<String>> getRequestMap() {
		return map;
	}
	
}
