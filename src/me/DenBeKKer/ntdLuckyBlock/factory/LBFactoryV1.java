package me.DenBeKKer.ntdLuckyBlock.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop.Special;

public class LBFactoryV1 implements LBFactory {
	
	public void generate(FileConfiguration file, LuckyBlockType type) { generate(file, type, 5, 50); }
	
	@SuppressWarnings("deprecation")
	public void generate(FileConfiguration file, LuckyBlockType type, int min, int max) {
		
		LBMain.log(Level.INFO, "\u00a7fGenerating \u00a7" + type.toColorSymbol() + type.name() + " LuckyBlock \u00a7fconfiguration \u00a76(LBF version: 1)");
		
		file.set("texture", type.getTexture());
		file.set("name", "&" + LBMain.toColor(type.asDye().getWoolData())
			+ String.valueOf(type.name().toCharArray()[0]).toUpperCase() + type.name().substring(1).toLowerCase().replace("_", " ") + " LuckyBlock");
		file.set("lore", Arrays.asList("&7Place me :D"));
		file.set("eco", true);
		file.set("shop", true);
		file.set("price", 250);
		file.set("animation", true);
		file.set("animation_type", "MOBSPAWNER_FLAMES");
		
		try {
			
			if(min < 1) min = 1;
			if(max < min) max = min;
			
			fillDrop(file, ThreadLocalRandom.current().nextInt(min, max + 1));
			
		} catch(Throwable th) {
			th.printStackTrace();
			LBMain.log(Level.SEVERE, "Generation for luckyblock " + type.name() + " failed. REPORT IT TO AUTHOR");
			
			try { file.set("drop", null); } catch(Exception ignored) {} 
			
			file.set("drop.0", Arrays.asList("COMMAND : me Hello! My name is %player%"));
			file.set("drop.1", Arrays.asList("CONSOLE : minecraft:give %player% diamond"));
			file.set("drop.2", Arrays.asList("MESSAGE : &7[&eLuckyBlock&7] &aHey! %player%, you are the best &6&l^_^"));
			
		}
		
	}
	
	public void fillDrop(FileConfiguration file, int amount) {
		
		for(int i = 0; i < amount; i++) {
			
			file.set("drop." + i, generateDrop());
			
		}
		
	}
	
	public List<String> generateDrop() {
		
		int random = ThreadLocalRandom.current().nextInt(0, 6);
		
		if (random == 0) return Arrays.asList(generateLuckyBlock());
		if (random == 4 && ThreadLocalRandom.current().nextBoolean()) return Arrays.asList(generateEntity());
		if (random == 5) return Arrays.asList(generateSpecial());
		
		List<String> collection = new ArrayList<>();
		for(int y = 0; y < random; y++)
			collection.add(generateItem());
		return collection;
		
	}
	
	public String generateSpecial() {
		
		Special random = Special.values()[ThreadLocalRandom.current().nextInt(Special.values().length)];
		
		if(random == Special.DIAMOND_COLUMN) {
			
			if(LBMain.getInstance().factory instanceof Mat1_13)
				return "SPECIAL : DIAMOND_COLUMN : RED_TERRACOTTA : YELLOW_TERRACOTTA : GREEN_TERRACOTTA : LIGHT_BLUE_TERRACOTTA : BLUE_TERRACOTTA : MAGENTA_TERRACOTTA";
			else {
				
				List<Special> special = new ArrayList<>(Arrays.asList(Special.values()));
				special.remove(Special.DIAMOND_COLUMN);
				random = special.get(ThreadLocalRandom.current().nextInt(special.size()));
				
			}
			
		}
		
		return "SPECIAL : " + random.name() + " : " + ThreadLocalRandom.current().nextInt((int)Math.floor((double)random.defaultValue() / 2),
				(int)Math.floor(random.defaultValue() * 1.5));
		
	}
	
	public String generateLuckyBlock() {
		return "LUCKY_BLOCK_ITEM : "
				+ (LuckyBlockType.values()[ThreadLocalRandom.current().nextInt(LuckyBlockType.values().length)]).name() +
					" : " + ThreadLocalRandom.current().nextInt(1, 5);
	}
	
	public String generateEntity() {
		return "ENTITY : " + (EntityType.values()[ThreadLocalRandom.current().nextInt(EntityType.values().length)]).name() + " : "
				+ ThreadLocalRandom.current().nextInt(1, 4);
	}
	
	public String generateItem() {
		
		List<Material> mat = null;
		
		try {
			mat = Arrays.asList(Material.values()).stream().filter(n -> n.isItem() && !n.isAir()).collect(Collectors.toList());
		} catch(Throwable th) {
			mat = Arrays.asList(Material.values()).stream().filter(n -> n.isItem() && !n.name().toUpperCase().contains("AIR")).collect(Collectors.toList());
		}
		
		if(LBMain.getInstance().factory instanceof Mat1_13)
			mat = mat.stream().filter(n -> !n.name().contains("LEGACY_")).collect(Collectors.toList());
		
		Material random = mat.get(ThreadLocalRandom.current().nextInt(mat.size()));
		
		int amount = ThreadLocalRandom.current().nextInt(random.getMaxStackSize() + 1);
		if(amount < 1)
			amount = 1;
		
		int data = random.getMaxStackSize();
		if(data > 0 && ThreadLocalRandom.current().nextBoolean())
			data = ThreadLocalRandom.current().nextInt((int)Math.floor((double)random.getMaxDurability() / 2), random.getMaxDurability() + 1);
		
		if(data < 0)
			data = 0;
		
		return "ITEM : " + random.name() + " : " + amount + " : " + data;
		
	}
	
}
