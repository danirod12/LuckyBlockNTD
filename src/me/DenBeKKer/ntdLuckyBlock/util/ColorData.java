package me.DenBeKKer.ntdLuckyBlock.util;

import org.bukkit.DyeColor;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;

public enum ColorData {
	
	WHITE((byte) 0),
	ORANGE((byte) 1),
	MAGENTA((byte) 2),
	LIGHT_BLUE((byte) 3),
	YELLOW((byte) 4),
	LIME((byte) 5),
	PINK((byte) 6),
	GRAY((byte) 7),
	LIGHT_GRAY((byte) 8),
	CYAN((byte) 9),
	PURPLE((byte) 10),
	BLUE((byte) 11), 
	BROWN((byte) 12),
	GREEN((byte) 13),
	RED((byte) 14),
	BLACK((byte) 15);
	
	private final byte data;
	
	ColorData(byte data) {
		this.data = data;
	}
	
	public byte getData() {
		return data;
	}
	
	public byte getDyeData() {
		return (byte) (15 - data);
	}
	
	public DyeColor asDyeColor() {
		return name().equalsIgnoreCase("LIGHT_GRAY") && LBMain.getInstance().factory instanceof Mat1_12
				? DyeColor.valueOf("SILVER") : DyeColor.valueOf(name());
	}
	
	@Deprecated
	public DyeColor asDyeColorByWool() {
		return DyeColor.getByWoolData(getData());
	}
	
	@Deprecated
	public DyeColor asDyeColorAsDyeData() {
		return DyeColor.getByDyeData((byte) getDyeData());
	}
	
}
