package me.DenBeKKer.ntdLuckyBlock.variables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
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
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockBreakEvent;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat.Mat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;

public class LuckyBlock {

	private LuckyBlockType type;
	
	private String texture, name, custom_name, exception;
	private List<String> lore = new ArrayList<>();
	private List<Collection<LuckyDrop>> items = new ArrayList<>();
	private boolean eco, shop, animation; private double price;
	
	public int items$mapped() { return items.size(); }
	public int items$mappedTwice() {
		int r = 0;
		for(Collection<LuckyDrop> drop : items)
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
				
				lore.add(str.replace("&", "\u00a7"));
				
		}
		
		for(String str : file.getConfigurationSection("drop").getKeys(false)) {
			
			Collection<LuckyDrop> collection = new ArrayList<>();
			for(String item : file.getStringList("drop." + str)) {
				LuckyDrop litem = LuckyBlockAPI.loadDrop(item);
				if(litem != null)
					collection.add(litem);
			}
			items.add(collection);
			
		}
		
	}
	
	public String getException() { return exception; }
	
	public void giveItem(Player p) { p.getInventory().addItem(getSkull()); }
	
	@SuppressWarnings("deprecation")
	public void placeBlock(Block block) {
		
		block.setType(Material.AIR);
		
		ArmorStand stand = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, -1.2, 0.5), EntityType.ARMOR_STAND);
		stand.setArms(false);
		stand.setCanPickupItems(false);
		stand.setCustomNameVisible(false);
		stand.setGravity(false);
		stand.setVisible(false);
		stand.setCustomName(type.name() + ";" + (int)stand.getLocation().getX() + ";" + (int)stand.getLocation().getY() + ";" + (int)stand.getLocation().getZ());
		
		stand.getEquipment().setHelmet(getSkull());
		
		block.setType(type.getMaterial());
		if(LBMain.getInstance().factory instanceof Mat1_12) {
			try {
				Method method = block.getClass().getDeclaredMethod("setData", byte.class);
				method.invoke(block, type.asDye().getWoolData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private ItemStack head = null;

	private Effect effect;
	
	public ItemStack getSkull() {
		
		if(head == null) {
			head = LBMain.getInstance().factory.getItem(Mat.PLAYER_SKULL, 1);
			
			if(LBMain.getDebug()) LBMain.debug("Loading skull texture");
			try {
				SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		        GameProfile profile = new GameProfile(type.getUUID(), null);
		        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/" + texture).getBytes());
		        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		        Field profileField = null;
				if(LBMain.getDebug()) LBMain.debug("Performing GameProfile");
		        profileField = headMeta.getClass().getDeclaredField("profile");
		        profileField.setAccessible(true);
		        profileField.set(headMeta, profile);
		        
		        headMeta.setDisplayName(name.replace("&", "\u00a7"));
		        headMeta.setLore(lore);
		        
		        head.setItemMeta(headMeta);
			} catch(Throwable t) {
				t.printStackTrace();
			}
			
		}
        return head;
        
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
		
		if(LBMain.getDebug())
			LBMain.debug("Try open LuckyBlock " + type.name()
				+ " (" + target.getName() + ", x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + ", " + ignore + ")");
		
		LuckyBlockBreakEvent event = new LuckyBlockBreakEvent(block, target, this);
		if(ignore) event.setIgnoreCancelled();
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return false;
		
		if(animation)
			block.getWorld().playEffect(block.getLocation().add(0.5, 0.5, 0.5), effect, 10);
		
		Collection<LuckyDrop> collection = items.get((int)(Math.random() * items.size()));
		if(LBMain.getDebug())
			LBMain.debug("Mathed " + collection.size() + " random lucky drops");
		for(LuckyDrop item : collection) {
			if(LBMain.getDebug()) {
				try {
					LBMain.debug("Performing item... " + new Gson().toJson(item));
				} catch(Throwable th) {
					LBMain.debug("Performing item... [Gson not found] - " + th.getLocalizedMessage());
				}
			}
			item.execute(block, target);
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
	
	public boolean tryOpen(Block block, boolean ignore) {
		
		if(LBMain.getDebug())
			LBMain.debug("Try open LuckyBlock " + type.name()
				+ " (not presented, x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + ", " + ignore + ")");
		
		LuckyBlockBreakEvent event = new LuckyBlockBreakEvent(block, this);
		if(ignore) event.setIgnoreCancelled();
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return false;
		
		if(animation)
			block.getWorld().playEffect(block.getLocation().add(0.5, 0.5, 0.5), effect, 10);
		
		block.setType(Material.AIR);
		for(LuckyDrop item : items.get((int)(Math.random() * items.size()))) {
			item.execute(block);
		}
		return true;
		
	}
	
	public String getCustomName() { return custom_name; }
	public boolean canBeShoped() { return shop; }
	
	public void addIntent(Collection<LuckyDrop> drop) { items.add(drop); }
	
	public LuckyBlockType getType() { return type; }
	
}
