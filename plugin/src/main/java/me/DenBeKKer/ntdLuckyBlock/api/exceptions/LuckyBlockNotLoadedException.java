package me.DenBeKKer.ntdLuckyBlock.api.exceptions;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;

public class LuckyBlockNotLoadedException extends Exception {
	
	public LuckyBlockNotLoadedException(LuckyBlockType type) {
		super(type.name() + " not loaded, you cant place it");
	}
	
	private static final long serialVersionUID = 194445144164630257L;
	
}
