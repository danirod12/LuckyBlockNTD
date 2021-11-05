package me.DenBeKKer.ntdLuckyBlock.customitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.Pair;

public class BekkerItemStackBuilder {
	
	private final Material material;
	private String name;
	private List<String> lore = new ArrayList<>();
	private List<Pair<Enchantment, Integer>> enchantments = new ArrayList<>();
	private HashMap<ItemEvent<?>, Object> events = new HashMap<>();
	private short data = -54;
	private Identifier identifier;
	private boolean he = false;
	
	@SuppressWarnings("deprecation")
	public BekkerItemStackBuilder(ItemStack item) {
		this(item.getType());
		setName(item.getItemMeta().getDisplayName());
		setLore(item.getItemMeta().getLore());
		he = item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS);
		this.enchantments = item.getEnchantments().entrySet().stream().map(n -> Pair.from(n)).collect(Collectors.toList());
		data = item.getDurability();
	}
	
	public BekkerItemStackBuilder(Material material) {
		this.material = material;
	}
	
	public BekkerItemStackBuilder setName(String name) {
		this.name = name == null ? null : name.replace("&", "\u00a7");
		return this;
	}
	
	public BekkerItemStackBuilder setLore(String... strings) {
		return setLore(Arrays.asList(strings));
	}
	
	public BekkerItemStackBuilder setLore(List<String> lore) {
		this.lore = lore == null ? new ArrayList<>() : lore.stream().map(n -> n.replace("&", "\u00a7")).collect(Collectors.toList());
		return this;
	}
	
	public BekkerItemStackBuilder setData(short data) {
		this.data = data;
		return this;
	}
	
	public <T> BekkerItemStackBuilder registerEvent(ItemEvent<T> event, T executor) {
		events.put(event, executor);
		return this;
	}
	
	protected BekkerItemStackBuilder setSerialID(String identifier) {
		this.identifier = new Identifier(LBMain.getInstance(), identifier);
		return this;
	}
	
	public BekkerItemStackBuilder setIdentifier(Plugin plugin, String identifier) {
		if(plugin instanceof LBMain)
			throw new UnsupportedOperationException("Cannot register items with LuckyBlock instance");
		this.identifier = new Identifier(plugin, identifier);
		return this;
	}
	
	public BekkerItemStackBuilder addUnsafeEnchantment(Enchantment e, int i) {
		enchantments.add(new Pair<Enchantment, Integer>(e, i));
		return this;
	}
	
	public ItemStack asItemStack() {
		
		try {
			
			@SuppressWarnings("deprecation")
			ItemStack item = data >= 0 ? new ItemStack(material, 1, data) : new ItemStack(material);
			ItemMeta meta = item.getItemMeta();
			if(name != null)
				meta.setDisplayName(name);
			if(lore != null)
				meta.setLore(lore);
			for(Pair<Enchantment, Integer> e : enchantments)
				meta.addEnchant(e.getKey(), e.getValue(), false);
			if(he) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			
			return item;
			
		} catch(Throwable th) {
			LBMain.log(Level.WARNING, "Report this to author (danirod12):");
			th.printStackTrace();
			LBMain.log(Level.WARNING, new Gson().toJson(this));
			return null;
		}
		
	}
	
	public BekkerItemStack build() {
		
		if(identifier == null) return null;
		return new BekkerItemStack(identifier, asItemStack(), events);
		
	}
	
	public BekkerItemStackBuilder hideEnchantments() {
		this.he = true;
		return this;
	}
	
	public Identifier getIdentifier() { return identifier; }
	
}
