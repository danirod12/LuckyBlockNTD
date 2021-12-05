package me.DenBeKKer.ntdLuckyBlock.variables;

import java.util.ArrayList;
import java.util.Collection;

import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;

public class LuckyEntry extends ArrayList<LuckyDrop> {
	
	private static final long serialVersionUID = -8843202586727811223L;
	private DropChance chance;
	
	public LuckyEntry(DropChance chance) {
		this.chance = chance;
	}
	
	public LuckyEntry() {
		this(DropChance.MEDIUM);
	}
	
	public LuckyEntry(DropChance chance, LuckyDrop... drop) {
		this(chance);
		for(LuckyDrop drop0 : drop)
			this.add(drop0);
	}
	
	public LuckyEntry(LuckyDrop... drop) {
		this();
		for(LuckyDrop drop0 : drop)
			this.add(drop0);
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
