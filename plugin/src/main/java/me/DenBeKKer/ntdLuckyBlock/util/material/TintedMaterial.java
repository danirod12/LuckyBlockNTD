package me.DenBeKKer.ntdLuckyBlock.util.material;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.TintedMaterialUnavailableException;
import org.bukkit.Material;

import java.util.logging.Level;

public class TintedMaterial {

	private final static boolean premium = LBMain.isPremium();
	private final boolean available;
	private final Material material;

	public TintedMaterial() {

		Material material = null;
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
		this.material = material;
		available = material != null;

	}
	
	public boolean isAvailable() {
		return is1_17();
	}
	
	public boolean is1_17() {
		return available && premium;
	}
	
	public Material getMaterial() {
		if(material == null)
			throw new TintedMaterialUnavailableException();
		return material;
	}
	
}
