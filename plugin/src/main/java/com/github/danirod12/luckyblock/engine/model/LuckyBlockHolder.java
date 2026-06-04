package com.github.danirod12.luckyblock.engine.model;

import com.github.danirod12.luckyblock.api.customitem.CustomItemFactory;
import com.github.danirod12.luckyblock.api.event.LuckyBlockBreakEvent;
import com.github.danirod12.luckyblock.api.model.*;
import com.github.danirod12.luckyblock.api.model.random.LuckyCollection;
import com.github.danirod12.luckyblock.api.setup.AnimationSetup;
import com.github.danirod12.luckyblock.api.setup.ILuckyRecipe;
import com.github.danirod12.luckyblock.api.setup.ShopSetup;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
import com.github.danirod12.luckyblock.util.Misc;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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

    @Getter
    @Setter
    private ItemsBag itemsBag = new ItemsBagImpl();

    private AnimationSetup animationSetup;
    private ShopSetup shopSetup;

    private ItemStack item;
    private ItemStack icon;

    private String customName;

    public LuckyBlockHolder(LuckyBlockEngine luckyBlockEngine, LuckyBlockKey type) {
        this.engine = luckyBlockEngine;
        this.type = type;
        this.identifier = new Identifier(CustomItemFactory.TAG_LUCKYBLOCK_TYPE, type.getKey());
        this.gson = new Gson();
    }

    public void verifyDataOrThrowException() {
        Objects.requireNonNull(this.item, "Executing item is not set");
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
//        if (!LuckyBlockAPI.getVersionType().isPremium()) { TODO?
//            this.engine.getLogChannel().warning("Your plugin version does not support icon assignment");
//            return;
//        }
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
        Misc.giveItemsOrDrop(player, stack);
    }

    @Override
    public ItemStack getItem() {
        return this.item == null ? null : this.item.clone();
    }

    @Override
    public ItemStack getIcon() {
        return (this.icon == null ? this.item : this.icon).clone();
    }

    @Override
    public List<ILuckyRecipe> getRecipes() {
        return new ArrayList<>(this.recipes);
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
        return this.customName == null ? this.getKey().getDefaultCustomName() : this.customName;
    }

    // LOGIC METHODS

    @Override
    public void placeBlock(Block block) {
        this.engine.placeBlockForce(this.type, block, this.getIcon());
    }

    @Override
    public boolean playOpen(Plugin instance, Block block, Player target, boolean dropItems, boolean ignoreCancel) {
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

        if (this.animationSetup != null && this.animationSetup.isEnabled()) {
            block.getWorld().playEffect(block.getLocation().add(0.5, 0.5, 0.5),
                    this.animationSetup.getEffect(), 10);
        }

        if (!event.isDrop()) {
            engine.getLogChannel().debug("LuckyBlock drop is cancelled by an event");
            return true;
        }
        if (this.itemsBag.isEmpty()) {
            engine.getLogChannel().debug("LuckyBlock has no items");
            return true;
        }

        for (LuckyCollection<LuckyDrop> entries : this.itemsBag.rollItems(target)) {
            for (LuckyDrop drop : entries.rollItems(target)) {
                if (engine.getLogChannel().isDebug()) {
                    try {
                        engine.getLogChannel().debug(this.gson.toJson(drop));
                    } catch (Exception exception) {
                        engine.getLogChannel().debug("Cannot debug " + drop.getClass().getName() + ":");
                        exception.printStackTrace();
                    }
                }
                engine.executeDrop(instance, drop, this.type, block, target);
            }
        }
        return true;
    }
}
