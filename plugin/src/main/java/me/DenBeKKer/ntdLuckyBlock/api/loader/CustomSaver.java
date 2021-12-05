package me.DenBeKKer.ntdLuckyBlock.api.loader;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public interface CustomSaver {
	
	String getDescription();
	
	static LuckyDrop load(String description) {
		throw new UnsupportedOperationException("Method not initialized");
	}
	
}
