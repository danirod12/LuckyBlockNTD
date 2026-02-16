package me.DenBeKKer.ntdLuckyBlock.engine.model;

import com.google.gson.Gson;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.event.LuckyBlockBreakEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ILuckyRecipe;
import me.DenBeKKer.ntdLuckyBlock.api.setup.ItemsBag;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.customitem.Identifier;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.nms.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.variables.setup.AnimationSetup;
import me.DenBeKKer.ntdLuckyBlock.variables.setup.ShopSetup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class LuckyBlockHolder implements LuckyBlock {

    private final LuckyBlockEngine engine;
    private final Gson gson;

    private final LuckyBlockKey type;
    private final Identifier identifier;
    private final List<ILuckyRecipe> recipes = new ArrayList<>();
    private final ItemsBag itemsBag;
    private AnimationSetup animationSetup;
    private ShopSetup shopSetup;
    private ItemStack item;
    private ItemStack icon;
    private String customName;

    public LuckyBlockHolder(LuckyBlockEngine luckyBlockEngine, LuckyBlockKey type) {
        this.engine = luckyBlockEngine;
        this.type = type;
        this.identifier = new Identifier(CustomItemFactory.TAG_LUCKYBLOCK_TYPE, type.getKey());
        if (LuckyBlockAPI.getVersionType().isPremium()) {
            throw new UnsupportedOperationException("Not implemented");
        } else {
            this.itemsBag = new SimpleItemsBag();
        }
        this.gson = new Gson();
    }

    public void verifyDataOrThrowException() {
        Objects.requireNonNull(this.item, "Executing item is not set");
        Objects.requireNonNull(this.customName, "Custom name is not set");
        Objects.requireNonNull(this.animationSetup, "AnimationSetup is not set");
        Objects.requireNonNull(this.shopSetup, "ShopSetup is not set");
    }

    // SETTERS

    @Override
    public void setItem(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        this.item = this.identifier.apply(itemStack);
    }

    @Override
    public void setIcon(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        if (!LuckyBlockAPI.getVersionType().isPremium()) {
            this.engine.getLogChannel().warning("Your plugin version does not support icon assignment");
            return;
        }
        this.icon = itemStack;
    }

    @Override
    public void removeRecipes(Predicate<ILuckyRecipe> predicate) {
        this.recipes.removeIf(predicate);
    }

    @Override
    public void addRecipes(ILuckyRecipe... recipes) {
        this.recipes.addAll(Arrays.asList(recipes));
    }

    @Override
    public void setShopSetup(ShopSetup shopSetup) {
        Objects.requireNonNull(shopSetup);
        this.shopSetup = shopSetup;
    }

    @Override
    public void setAnimationSetup(AnimationSetup animationSetup) {
        Objects.requireNonNull(animationSetup);
        this.animationSetup = animationSetup;
    }

    @Override
    public void setCustomName(String customName) {
        Objects.requireNonNull(customName);
        this.customName = customName;
    }

    // GETTERS

    @Override
    public LuckyBlockKey getKey() {
        return type;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void giveItem(Player player, int amount) {
        ItemStack stack = this.getItem();
        if (stack == null) {
            return;
        }
        stack.setAmount(amount);
        player.getInventory().addItem(stack);
    }

    @Override
    public ItemStack getItem() {
        return this.item == null ? null : this.item.clone();
    }

    @Override
    public ItemStack getIcon() {
        return this.icon == null ? null : this.icon.clone();
    }

    @Override
    public List<ILuckyRecipe> getRecipes() {
        return new ArrayList<>(this.recipes);
    }

    @Override
    public ItemsBag getItemsBag() {
        return this.itemsBag;
    }

    @Override
    public ShopSetup getShopSetup() {
        return this.shopSetup;
    }

    @Override
    public AnimationSetup getAnimationSetup() {
        return this.animationSetup;
    }

    @Override
    public String getCustomName() {
        return this.customName;
    }

    // LOGIC METHODS

    @Override
    public void placeBlock(Block block) {
        block.setType(Material.AIR);

        ArmorStand stand = (ArmorStand) block.getWorld().spawnEntity(
                engine.getAssociatedLocation(block), EntityType.ARMOR_STAND);
        stand.setArms(false);
        stand.setCanPickupItems(false);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);
        stand.setCustomName(this.engine.getLocatedName(type, stand.getLocation()));

        EntityEquipment entityEquipment = stand.getEquipment();
        assert entityEquipment != null;
        if (this.icon != null) {
            throw new UnsupportedOperationException("Not implemented");
        } else {
            entityEquipment.setHelmet(this.item);
        }

        // Light source feature
        // The trick is based on fire effect that is not rendering on client if stand is marker
        if (this.engine.isLightSource()) {
            if (this.engine.getVersionControl().isLegacy()) {
                // 1.8 - 1.12 ArmorStand (Or even Entity) meta apply is delayed
                // If we do not give 2 ticks delay, client will play fire animation
                Bukkit.getScheduler().runTaskLater(this.engine.getLogChannel().getPlugin(),
                        () -> stand.setFireTicks(Integer.MAX_VALUE), 2L);
            } else {
                stand.setFireTicks(Integer.MAX_VALUE);
            }
        }

        // Update block. If we are on legacy MC version, apply block data
        block.setType(type.getMaterial());
        if (this.engine.getVersionControl().isLegacy() && type.isApplyColorDataToBlock()) {
            IMat.setData(block, type.getColorData().getData());
        }
    }

    @Override
    public boolean playOpen(Block block, Player target, boolean dropItems, boolean ignoreCancel) {
        if (engine.getLogChannel().isDebug()) {
            engine.getLogChannel().debug("Playing LuckyBlock " + type.getKey() + " open as "
                    + (target == null ? "not presented" : target.getName()) + " at {x:" + block.getX() + ", y:"
                    + block.getY() + ", z:" + block.getZ() + "}" + (ignoreCancel ? ", ignoring event cancel" : ""));
        }

        LuckyBlockBreakEvent event = new LuckyBlockBreakEvent(block, target, this);
        event.setDrop(dropItems);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() && !ignoreCancel) {
            engine.getLogChannel().debug("Event is cancelled, skipping...");
            return false;
        }

        if (this.animationSetup.isEnabled()) {
            block.getWorld().playEffect(block.getLocation().add(0.5, 0.5, 0.5),
                    this.animationSetup.getEffect(), 10);
        }

        if (!event.isDrop()) {
            engine.getLogChannel().debug("LuckyBlock drop is cancelled by an event");
            return true;
        }
        if (this.itemsBag.size() <= 0) {
            engine.getLogChannel().debug("LuckyBlock has no items");
            return true;
        }
        for (LuckyDrop luckyDrop : this.itemsBag.getEntry()) {
            if (engine.getLogChannel().isDebug()) {
                try {
                    engine.getLogChannel().debug(this.gson.toJson(luckyDrop));
                } catch (Exception exception) {
                    engine.getLogChannel().debug("Cannot debug " + luckyDrop.getClass().getName() + ":");
                    exception.printStackTrace();
                }
            }
            engine.executeDrop(luckyDrop, this.type, block, target);
        }
        return true;
    }
}
