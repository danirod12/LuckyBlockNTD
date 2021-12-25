package me.DenBeKKer.ntdLuckyBlock.api.exceptions;

public class TintedMaterialUnavailableException extends RuntimeException {
	
	private static final long serialVersionUID = 9133682970404739412L;
	
	public TintedMaterialUnavailableException() {
		super("Your platform not support Tinted glass (lower than 1.17) or your version not a premium one");
	}
	
}
