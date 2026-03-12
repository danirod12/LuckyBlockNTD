package me.DenBeKKer.ntdLuckyBlock.api.customitem;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.model.Identifier;
import me.DenBeKKer.ntdLuckyBlock.api.util.Pair;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BekkerItemStackBuilder {

    @SerializedName(value = "material")
    private final Material material;
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "lore")
    private List<String> lore = new ArrayList<>();
    @SerializedName(value = "enchantments")
    private List<Pair<Enchantment, Integer>> enchantments = new ArrayList<>();
    @SerializedName(value = "events")
    private HashMap<ItemEvent<?>, Consumer<Event>> events = new HashMap<>();
    @SerializedName(value = "data")
    private short data = -54;
    @Getter
    @SerializedName(value = "identifier")
    private Identifier identifier;
    @SerializedName(value = "hide_enchantments")
    private boolean hideEnchantments = false;

    @SuppressWarnings("deprecation")
    public BekkerItemStackBuilder(ItemStack item) {
        this(item.getType());
        setName(item.getItemMeta().getDisplayName());
        setLore(item.getItemMeta().getLore());
        hideEnchantments = item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS);
        this.enchantments = item.getEnchantments().entrySet().stream().map(Pair::from).collect(Collectors.toList());
        data = item.getDurability();
    }

    public BekkerItemStackBuilder(Material material) {
        this.material = material;
    }

    public BekkerItemStackBuilder setName(String name) {
        this.name = name == null ? null : name.replace("&", "§");
        return this;
    }

    public BekkerItemStackBuilder setLore(String... strings) {
        return setLore(Arrays.asList(strings));
    }

    public BekkerItemStackBuilder setLore(List<String> lore) {
        this.lore = lore == null ? new ArrayList<>()
                : lore.stream().map(n -> n.replace("&", "§")).collect(Collectors.toList());
        return this;
    }

    public BekkerItemStackBuilder setData(short data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E> BekkerItemStackBuilder registerEvent(ItemEvent<E> event, Consumer<E> executor) {
        events.put(event, (Consumer<Event>) executor);
        return this;
    }

    protected BekkerItemStackBuilder setSerialID(String identifier) {
        this.identifier = new Identifier(LuckyBlockAPI.getInstance(),
                CustomItemFactory.TAG_IDENTIFIER_NAME, identifier);
        return this;
    }

    public BekkerItemStackBuilder setIdentifier(Plugin plugin, String identifier) {
        if (LuckyBlockAPI.getInstance().getName().equalsIgnoreCase(plugin.getName())) {
            throw new UnsupportedOperationException("Cannot register items with LuckyBlock instance");
        }
        this.identifier = new Identifier(plugin, CustomItemFactory.TAG_IDENTIFIER_NAME, identifier);
        return this;
    }

    public BekkerItemStackBuilder addUnsafeEnchantment(Enchantment e, int i) {
        enchantments.add(new Pair<>(e, i));
        return this;
    }

    public ItemStack asItemStack() {
        try {
            @SuppressWarnings("deprecation")
            ItemStack item = data >= 0 ? new ItemStack(material, 1, data) : new ItemStack(material);

            ItemMeta meta = item.getItemMeta();
            if (name != null) {
                meta.setDisplayName(name);
            }
            if (lore != null) {
                meta.setLore(lore);
            }
            for (Pair<Enchantment, Integer> e : enchantments) {
                meta.addEnchant(e.getKey(), e.getValue(), false);
            }
            if (hideEnchantments) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);

            return item;
        } catch (Throwable th) {
            th.printStackTrace();
            LuckyBlockAPI.getLogger().log(Level.WARNING, new Gson().toJson(this));
            return null;
        }
    }

    public BekkerItemStack build() {
        if (identifier == null) {
            return null;
        }
        return new BekkerItemStack(identifier, asItemStack(), events);
    }

    public BekkerItemStackBuilder hideEnchantments() {
        this.hideEnchantments = true;
        return this;
    }
}
