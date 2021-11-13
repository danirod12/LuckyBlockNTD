package me.DenBeKKer.ntdLuckyBlock.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockBreakEvent;
import me.DenBeKKer.ntdLuckyBlock.recipe.CraftTheory;
import me.DenBeKKer.ntdLuckyBlock.recipe.LuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;

public class LuckyBlock {
	
	private final LuckyBlockType type;
	
	private final String exception, texture, name, custom_name;
	private List<String> lore = new ArrayList<>();
	private List<LuckyEntry> items = new ArrayList<>();
	private final boolean eco, shop, animation, cdef, ccus;
	private double price;
	
	public void setPrice(double price, boolean update) {
		this.price = price;
		if(update) {
			Config config = new Config(LBMain.getInstance(), LBMain.getFolder(), type.name().toLowerCase() + ".yml");
			if(config.getFile().exists()) {
				config.reload();
				FileConfiguration file = config.get();
				if(file != null) {
					file.set("price", price);
					config.save();
					LBMain.log(Level.INFO, "Force updated LuckyBlock price");
				}
			}
		}
	}
	
	public int items$mapped() { return items.size(); }
	public int items$mappedTwice() {
		int r = 0;
		for(LuckyEntry drop : items)
			r += drop.size();
		return r;
	}
	
	public boolean canBeSold() { return eco && LBMain.getInstance().eco != null; }
	public double getPrice() { return price; }
	
	public LuckyBlock(LuckyBlockType type, Config config) {
		
		this.type = type;
		FileConfiguration file = config.get();
		if(!file.isSet("texture")) {
			exception = "texture not set";
			texture = name = custom_name = null;
			eco = shop = animation = cdef = ccus = false;
			return;
		}
		boolean b = false;
		if(!file.isSet("texture")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <texture> not found. Creating...");
			file.set("texture", type.getTexture());
		}
		if(!file.isSet("eco")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <eco> not found. Creating...");
			file.set("eco", true);
		}
		if(!file.isSet("animation")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <animation> not found. Creating...");
			file.set("animation", true);
		}
		if(!file.isSet("animation_type")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <animation_type> not found. Creating...");
			file.set("animation_type", "MOBSPAWNER_FLAMES");
		}
		if(!file.isSet("shop")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <shop> not found. Creating...");
			file.set("shop", true);
		}
		if(!file.isSet("price")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <price> not found. Creating...");
			file.set("price", 250);
		}
		if(!file.isSet("craft.default")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <craft.default> not found. Creating...");
			file.set("craft.default", true);
		}
		if(!file.isSet("craft.custom")) { b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <craft.custom> not found. Creating...");
			file.set("craft.custom", LBMain.isPremium());
		}
		if(config.need_save() || b) config.save();
		config.deleteDefault();
		
		texture = file.getString("texture");
		eco = file.getBoolean("eco");
		animation = file.getBoolean("animation");
		shop = file.getBoolean("shop");
		price = file.getDouble("price");
		try {
			effect = Effect.valueOf(file.getString("animation_type").toUpperCase());
		} catch(Exception ex) {
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Animation " + file.getString("animation_type") + " was not found, check " +
					"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Effect.html for full animations list");
			effect = Effect.MOBSPAWNER_FLAMES;
		}
		name = file.isSet("name") ? type.fixName(file.getString("name")) : type.name();
		custom_name = file.isSet("custom_name") ? file.getString("custom_name") : type.name();
		
		if(file.isSet("lore"))
			for(String str : file.getStringList("lore")) {
				
				lore.add(Misc.setColors(str));
				
		}
		
		cdef = file.getBoolean("craft.default");
		ccus = file.getBoolean("craft.custom");
		
		for(String str : file.getConfigurationSection("drop").getKeys(false))
			addIntent(LuckyBlockAPI.loadDropEntry(config, "drop." + str));
		exception = null;
		
	}
	
	public void loadRecipes() {
		
		recipes = new ArrayList<>();
		
		if(cdef) {
			recipes.addAll(CraftTheory.getDefault(type));
		}
		
		if(ccus) {
			
			if(LBMain.isPremium()) {
				recipes.addAll(CraftTheory.getFromConfig(type));
			} else {
				LBMain.log(Level.WARNING, "Custom crafts feature available only for premium version");
				LBMain.log(Level.WARNING, "Check out premium plugin version - https://www.spigotmc.org/resources/94872");
			}
			
		}
		
	}
	
	public String getException() { return exception; }
	
	public void giveItem(Player p) { p.getInventory().addItem(getSkull()); }
	
	@Deprecated
	public void placeBlock(Block block) {
		placeBlock(block, false);
	}
	
	public void placeBlock(Block block, boolean check) {
		
		if(check && !LBMain.getInstance().factory.isSkull(block.getType())) {
			if(LBMain.isDebug())
				LBMain.debug("Operation LuckyBlock.placeBlock(block, true) canceled because " + block.getType() + " not a skull");
			return;
		}
		block.setType(Material.AIR);
		
		ArmorStand stand = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, -1.2, 0.5), EntityType.ARMOR_STAND);
		stand.setArms(false);
		stand.setCanPickupItems(false);
		stand.setCustomNameVisible(false);
		stand.setGravity(false);
		stand.setVisible(false);
		stand.setMarker(true);
		stand.setCustomName(type.name() + ";" + (int)stand.getLocation().getX() + ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ());
		
		stand.getEquipment().setHelmet(getSkull());
		
		block.setType(type.getMaterial());
		if(LBMain.getInstance().factory instanceof Mat1_12)
			IMat.setData(block, (byte) type.asColor().getData());
		
	}
	
	private ItemStack head = null;

	private Effect effect;
	
	private Collection<LuckyRecipe> recipes = new ArrayList<>();
	
	private List<DropChance> chances = new ArrayList<>();
	
	public void resetSkull() { head = null; }
	
	public ItemStack getSkull() {
		
		if(head == null)
			head = LBMain.getHead0("http://textures.minecraft.net/texture/" + texture, Misc.setColors(name), lore, type.getUUID());
        return head.clone();
        
	}
	
	@Deprecated
	public void open(Block block, Player target) { open(block, target, false); }
	
	@Deprecated
	public void open(Block block, Player target, boolean ignore) {
		if(!tryOpen(block, target, ignore))
			LBMain.log(Level.WARNING, "[!] DO NOT REPORT IT TO LUCKYBLOCK NTD AUTHOR. IT IS NOT A BUG FROM MY PLUGIN. LUCKYBLOCK"
					+ " {w:" + block.getWorld().getName() + ", x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + "} "
							+ "RECEIVED INCORRECT BREAK API METHOD AND BECOME CORRUPTED [!]");
	}
	
	public boolean tryOpen(Block block, Player target, boolean ignore) {
		
		if(LBMain.isDebug())
			LBMain.debug("Try open LuckyBlock " + type.name()
				+ " (" + (target == null ? "not presented" : target.getName())
				+ ", x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + ", " + ignore + ")");
		
		LuckyBlockBreakEvent event = target == null ? new LuckyBlockBreakEvent(block, this) : new LuckyBlockBreakEvent(block, target, this);
		
		if(target != null) {
			
			if(!LBMain.getInstance().w.allowed_break(block.getWorld())) {
				
				if(LBMain.getInstance().w.getBreakNoDrop()) {
					event.setDrop(false);
				} else {
					target.sendMessage(Message.CANT_INTERACT_WORLD.getAsString(true).replace("%world%", block.getWorld().getName()));
					return false;
				}
				
			}
			
		}
		
		if(ignore) event.setIgnoreCancelled();
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return false;
		if(!event.isDrop()) return true;
		
		if(animation)
			block.getWorld().playEffect(block.getLocation().add(0.5, 0.5, 0.5), effect, 10);
		
		LuckyEntry collection = DropChance.random(chances).roll(items);
		LBMain.debug("Mathed " + collection.size() + " random lucky drops");
		for(LuckyDrop item : collection) {
			if(LBMain.isDebug()) {
				try {
					LBMain.debug("Performing item... " + new Gson().toJson(item));
				} catch(Throwable th) {
					LBMain.debug("Performing item... [Gson throwable] - " + th.getLocalizedMessage());
				}
			}
			item.executeProtected(block, target);
		}
		return true;
		
	}
	
	@Deprecated
	public void open(Block block) { open(block, false); }
	
	@Deprecated
	public void open(Block block, boolean ignore) {
		if(!tryOpen(block, ignore))
			LBMain.log(Level.WARNING, "[!] DO NOT REPORT IT TO LUCKYBLOCK NTD AUTHOR. IT IS NOT A BUG FROM MY PLUGIN. LUCKYBLOCK"
					+ " {w:" + block.getWorld().getName() + ", x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + "} "
							+ "RECEIVED INCORRECT BREAK API METHOD AND BECOME CORRUPTED [!]");
	}
	
	public boolean tryOpen(Block block, boolean ignore) { return tryOpen(block, null, ignore); }
	
	public String getCustomName() { return custom_name; }
	public boolean canBeShoped() { return shop; }
	
	public void addIntent(LuckyEntry drop) {
		items.add(drop);
		if(!chances.contains(drop.getDropChance()))
			chances.add(drop.getDropChance());
	}
	
	public LuckyBlockType getType() { return type; }
	
	public Collection<LuckyRecipe> getRecipes() { return recipes; }
	
}
