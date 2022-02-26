package me.DenBeKKer.ntdLuckyBlock.variables;

import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LuckyEntry extends ArrayList<LuckyDrop> {

	private DropChance chance;
	
	public LuckyEntry(DropChance chance) {
		this.chance = chance;
	}
	
	public LuckyEntry() {
		this(DropChance.MEDIUM);
	}
	
	public LuckyEntry(DropChance chance, LuckyDrop... drop) {
		this(chance);
		this.addAll(Arrays.asList(drop));
	}
	
	public LuckyEntry(LuckyDrop... drop) {
		this();
		this.addAll(Arrays.asList(drop));
	}
	
	public LuckyEntry(Collection<LuckyDrop> drop) {
		this();
		this.addAll(drop);
	}
	
	public LuckyEntry(DropChance chance, Collection<LuckyDrop> drop) {
		this(chance);
		this.addAll(drop);
	}
	
	public void setChance(DropChance chance) {
		this.chance = chance;
	}
	
	public DropChance getDropChance() {
		return LuckyBlockAPI.isPremium() ? chance : DropChance.MEDIUM;
	}
	
}
