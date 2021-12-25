package me.DenBeKKer.ntdLuckyBlock.api;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyEntry;

public enum DropChance {
	
	/**
	 * ~5.3%
	 */
	LOWEST(1),
	/**
	 * ~15.8%
	 */
	LOW(3),
	/**
	 * ~26.3%
	 */
	MEDIUM(5),
	/**
	 * ~52.6%
	 */
	HIGH(10);
	
	private final int i;

	public int i() { return i; }

	DropChance(int i) {
		this.i = i;
	}
	
	public static DropChance random(List<DropChance> chances) {
		
		if(chances.size() == 0)
			throw new UnsupportedOperationException("Chances mismatch, random() got an empty List");
		
		if(chances.size() == 1)
			return chances.get(0);
		
		int a = 0;
		for(DropChance chance : chances)
			a += chance.i;
		DropChance[] chance = new DropChance[a];
		a = 0;
		for(DropChance chance0 : chances) {
			for(int j = 0; j < chance0.i; j++) {
				chance[a] = chance0;
				a++;
			}
		}
		return chance[ThreadLocalRandom.current().nextInt(chance.length)];
		
	}

	public static int chance(List<DropChance> chances, DropChance chance) {

		if(chances.size() == 0)
			throw new UnsupportedOperationException("Chances mismatch, random() got an empty List");

		if(!chances.contains(chance)) return 0;
		if(chances.size() == 1) return 100;

		int a = 0;
		for(DropChance c : chances)
			a += chance.i;
		return chance.i * 100 / a;

	}
	
	public LuckyEntry roll(List<LuckyEntry> items) {
		List<LuckyEntry> list = items.stream().filter(n -> n.getDropChance() == this).collect(Collectors.toList());
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}
	
}
