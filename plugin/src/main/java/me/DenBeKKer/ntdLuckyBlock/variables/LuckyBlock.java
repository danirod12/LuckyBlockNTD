package me.DenBeKKer.ntdLuckyBlock.variables;

import com.google.gson.Gson;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.events.LuckyBlockBreakEvent;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.customitem.Identifier;
import me.DenBeKKer.ntdLuckyBlock.recipe.CraftTheory;
import me.DenBeKKer.ntdLuckyBlock.recipe.LuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;
import me.DenBeKKer.ntdLuckyBlock.variables.drop.ItemDrop;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LuckyBlock {

	private final LuckyBlockType type;
	private final Identifier identifier;

	private final String exception, texture, name, custom_name;
	private List<String> lore = new ArrayList<>();
	private List<LuckyEntry> items = new ArrayList<>();
	private final boolean eco, shop, animation, cdef, ccus;
	private double price;

	public void setPrice(double price, boolean update) {
		this.price = price;
		if (update) {
			Config config = new Config(LBMain.getInstance(), LBMain.getFolder(), type.name().toLowerCase() + ".yml");
			if (config.getFile().exists()) {
				config.reload();
				FileConfiguration file = config.get();
				if (file != null) {
					file.set("price", price);
					config.save();
					LBMain.log(Level.INFO, "Force updated LuckyBlock price");
				}
			}
		}
	}

	public int items$mapped() {
		return items.size();
	}

	public int items$mappedTwice() {
		int r = 0;
		for (LuckyEntry drop : items)
			r += drop.size();
		return r;
	}

	public boolean canBeSold() {
		return eco && LBMain.getInstance().getEconomyBridge() != null
				&& LBMain.getInstance().getEconomyBridge().enabled();
	}

	public double getPrice() {
		return price;
	}

	public LuckyBlock(LuckyBlockType type, Config config) {

		this.type = type;
		this.identifier = new Identifier(CustomItemFactory.TAG_LUCKYBLOCK_TYPE, type.name().toLowerCase());
		FileConfiguration file = config.get();
		if (!file.isSet("texture")) {
			exception = "texture not set";
			texture = name = custom_name = null;
			eco = shop = animation = cdef = ccus = false;
			return;
		}
		boolean b = false;
		if (!file.isSet("texture")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <texture> not found. Creating...");
			file.set("texture", type.getTexture());
		}
		if (!file.isSet("eco")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <eco> not found. Creating...");
			file.set("eco", true);
		}
		if (!file.isSet("animation")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <animation> not found. Creating...");
			file.set("animation", true);
		}
		if (!file.isSet("animation_type")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <animation_type> not found. Creating...");
			file.set("animation_type", "MOBSPAWNER_FLAMES");
		}
		if (!file.isSet("shop")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <shop> not found. Creating...");
			file.set("shop", true);
		}
		if (!file.isSet("price")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <price> not found. Creating...");
			file.set("price", 250);
		}
		if (!file.isSet("craft.default")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <craft.default> not found. Creating...");
			file.set("craft.default", true);
		}
		if (!file.isSet("craft.custom")) {
			b = true;
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Option for path <craft.custom> not found. Creating...");
			file.set("craft.custom", LBMain.isPremium());
		}
		if (b) config.save();

		texture = file.getString("texture");
		eco = file.getBoolean("eco");
		animation = file.getBoolean("animation");
		shop = file.getBoolean("shop");
		price = file.getDouble("price");
		try {
			effect = Effect.valueOf(file.getString("animation_type").toUpperCase());
		} catch (Exception ex) {
			LBMain.getInstance().getLogger().log(Level.WARNING, "[" + type.name() + "] Animation " + file.getString("animation_type") + " was not found, check " +
					"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Effect.html for full animations list");
			effect = Effect.MOBSPAWNER_FLAMES;
		}
		name = file.isSet("name") ? file.getString("name") : type.name();
		custom_name = file.isSet("custom_name") ? file.getString("custom_name") : type.name();

		if (file.isSet("lore"))
			for (String str : file.getStringList("lore")) {

				lore.add(Misc.setColors(str));

			}

		cdef = file.getBoolean("craft.default");
		ccus = file.getBoolean("craft.custom");

		for (String str : file.getConfigurationSection("drop").getKeys(false))
			addIntent(LuckyBlockAPI.loadDropEntry(config, "drop." + str));
		exception = null;

	}

	public void loadRecipes() {
		recipes = new ArrayList<>();
		if (cdef) {
			recipes.addAll(CraftTheory.getDefault(type));
		}
		if (ccus) {
			recipes.addAll(CraftTheory.getFromConfig(type));
		}
	}

	public String getException() {
		return exception;
	}

	public void giveItem(Player p) {
		Misc.giveItemsOrDrop(p, getSkull());
	}

	@Deprecated
	public void placeBlock(Block block) {
		placeBlock(block, false);
	}

	public void placeBlock(Block block, boolean check) {
		if (check && !LBMain.getInstance().factory.isSkull(block.getType())) {
			if (LBMain.isDebug())
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
		stand.setCustomName(type.getLocatedName(stand.getLocation()));

		stand.getEquipment().setHelmet(getSkull());

		if (LBMain.getInstance().isLightSource()) {
			if (LBMain.getInstance().factory instanceof Mat1_12) {
				Bukkit.getScheduler().runTaskLater(LBMain.getInstance(),
						() -> stand.setFireTicks(Integer.MAX_VALUE), 2L);
			} else {
				stand.setFireTicks(Integer.MAX_VALUE);
			}
		}

		block.setType(type.getMaterial());
		if (LBMain.getInstance().factory instanceof Mat1_12 && type.isColoredGlass())
			IMat.setData(block, (byte) type.asColor().getData());
	}

	private ItemStack head = null;

	private Effect effect;

	private Collection<LuckyRecipe> recipes = new ArrayList<>();

	private List<DropChance> chances = new ArrayList<>();

	public void resetSkull() {
		head = null;
	}

	public ItemStack getSkull() {
		if (head == null)
			head = identifier.apply(Misc.getPlayerHead("http://textures.minecraft.net/texture/" + texture,
					Misc.setColors(name), lore, type.getUUID()));
		return head.clone();
	}

	public boolean isSkull(ItemStack stack) {
		return isSkull(stack, true);
	}

	public boolean isSkull(ItemStack stack, boolean check_tag) {
		return isSkull(stack, true, check_tag);
	}

	public boolean isSkull(ItemStack stack, boolean check_uuid, boolean check_tag) {
		return LuckyBlockAPI.parseLuckyBlock(stack, check_uuid, check_tag) == this.type;
	}

	public boolean tryOpen(Block block, Player target, boolean ignore) {

		if (LBMain.isDebug())
			LBMain.debug("Try open LuckyBlock " + type.name()
					+ " (" + (target == null ? "not presented" : target.getName())
					+ ", x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + ", " + ignore + ")");

		LuckyBlockBreakEvent event = new LuckyBlockBreakEvent(block, target, this);

		if (target != null) {

			if (!LBMain.getInstance().worldsFilter.isEnabled(block.getWorld().getName())) {

				if (LBMain.getInstance().worldsFilter.getDataHandler().getBreakNoDrop()) {
					event.setDrop(false);
				} else {
					target.sendMessage(Message.CANT_INTERACT_WORLD.getAsString(true).replace("%world%", block.getWorld().getName()));
					return false;
				}

			}

		}

		if (ignore) event.setIgnoreCancelled();
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		if (!event.isDrop()) return true;

		if (animation)
			block.getWorld().playEffect(block.getLocation().add(0.5, 0.5, 0.5), effect, 10);

		if (items.size() == 0) {
			LBMain.debug("No items to drop");
			return true;
		}
		LuckyEntry collection = DropChance.random(chances).roll(items);
		LBMain.debug("Mathed " + collection.size() + " random lucky drops");
		for (LuckyDrop item : new ArrayList<>(collection)) {
			if (LBMain.isDebug()) {
				try {
					LBMain.debug("Performing item... " + new Gson().toJson(item));
				} catch (Throwable th) {
					LBMain.debug("Performing item... [Gson throwable] - " + th.getLocalizedMessage());
				}
			}
			item.executeProtected(type, block, target);
		}
		return true;

	}

	@Deprecated
	public void open(Block block) {
		open(block, false);
	}

	@Deprecated
	public void open(Block block, boolean ignore) {
		if (!tryOpen(block, ignore))
			LBMain.log(Level.WARNING, "[!] DO NOT REPORT IT TO LUCKYBLOCK NTD AUTHOR. IT IS NOT A BUG FROM MY PLUGIN. LUCKYBLOCK"
					+ " {w:" + block.getWorld().getName() + ", x:" + block.getX() + ", y:" + block.getY() + ", z:" + block.getZ() + "} "
					+ "RECEIVED INCORRECT BREAK API METHOD AND BECOME CORRUPTED [!]");
	}

	public boolean tryOpen(Block block, boolean ignore) {
		return tryOpen(block, null, ignore);
	}

	public String getCustomName() {
		return custom_name;
	}

	public boolean canBeShoped() {
		return shop;
	}

	public void addIntent(LuckyEntry drop) {
		if (drop == null || drop.isEmpty()) return;
		items.add(drop);
		if (!chances.contains(drop.getDropChance()))
			chances.add(drop.getDropChance());
	}

	public void remove(ItemDrop drop) {
		for (LuckyEntry item : new ArrayList<>(items)) {
			if (item.remove(drop)) {
				if (item.size() == 0) {
					items.remove(item);
					chances.clear();
					chances.addAll(items.stream().map(LuckyEntry::getDropChance)
							.distinct().collect(Collectors.toList()));
				}
				break;
			}
		}
	}

	public LuckyBlockType getType() {
		return type;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Collection<LuckyRecipe> getRecipes() {
		return recipes;
	}

	@SuppressWarnings("deprecation")
	public String getOldName() {
		return type.fixName(name);
	}

	public String getName() {
		return name;
	}

}
