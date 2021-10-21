package me.DenBeKKer.ntdLuckyBlock.util.material;

import java.util.logging.Level;

import org.bukkit.Material;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.TintedMaterialUnavailableException;

public class TintedMaterial {
	
	private final static boolean available;
	private final static boolean premium = LBMain.isPremium();
	private static Material material;
	
	static {
		
		try {
			
			material = Material.valueOf("TINTED_GLASS");
			
		} catch(Exception lower1_17) {
			
			material = null;
			LBMain.log(Level.INFO, "Server version not support Tinted LuckyBlock. Skipping...");
			
		}
		
		if(material != null && !premium) {
			
			material = null;
			LBMain.log(Level.INFO, "Tinted LuckyBlock disabled in free version. Skipping...");
			
		}
		
		available = material != null;
		
	}
	
	public static boolean isAvailable() {
		return is1_17();
	}
	
	public static boolean is1_17() {
		return available && premium;
	}
	
	public static Material getMaterial() {
		if(material == null)
			throw new TintedMaterialUnavailableException();
		return material;
	}
	
}
