package me.DenBeKKer.ntdLuckyBlock.factory;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public abstract interface LBFactory {
	
	public static int latest_version = 1;
	
	public static LBFactory latest() {
		
		try {
			return get(latest_version);
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	public static LBFactory get(int version) throws ClassNotFoundException {
		
		switch(version) {
		case 1: return new LBFactoryV1();
		default: throw new ClassNotFoundException("Factory version " + version + " not found. Lastest version is " + latest_version);
		}
		
	}
	
	String generateLuckyBlock();
	
	String generateItem();
	
	String generateSpecial();
	
	String generateEntity();
	
	List<String> generateDrop();
	
	void generate(FileConfiguration file, LuckyBlockType type);
	
	void generate(FileConfiguration file, LuckyBlockType type, int min, int max);
	
}
