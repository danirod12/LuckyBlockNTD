package me.DenBeKKer.ntdLuckyBlock.api.loader;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public interface CustomSaver {
	
	String getDescription();
	
	LuckyDrop load(String description);
	
}
