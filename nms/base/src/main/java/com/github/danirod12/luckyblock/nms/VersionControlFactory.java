package com.github.danirod12.luckyblock.nms;

import com.github.danirod12.luckyblock.api.model.Identifier;
import com.github.danirod12.luckyblock.api.provider.VersionControl;
import com.github.danirod12.luckyblock.api.util.JavaUtils;
import com.github.danirod12.luckyblock.api.util.LogChannel;
import com.github.danirod12.luckyblock.nms.material.IMat;
import com.github.danirod12.luckyblock.nms.material.Mat1_12;
import com.github.danirod12.luckyblock.nms.material.Mat1_13;
import com.github.danirod12.mcversion.MinecraftVersion;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class VersionControlFactory implements VersionControl {

    private static final String PROFILE_NAME;

    static {
        String profileName;
        try {
            // Causes NullPointerException since 1.20.2
            new GameProfile(UUID.randomUUID(), null);
            profileName = null;
        } catch (NullPointerException exception) {
            profileName = "";
        }
        PROFILE_NAME = profileName;
    }

    private static final Function<SkullMeta, UUID> UUID_RETRIEVER;

    static {
        ItemStack skullExample;
        try {
            // 1.13+
            skullExample = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException exception) {
            // legacy method
            skullExample = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        Class<?> skullMetaClazz = skullExample.getItemMeta().getClass();

        Function<SkullMeta, GameProfile> profileRetriever = null;
        for (Field field : skullMetaClazz.getDeclaredFields()) {
            if (field.getType() == GameProfile.class) {
                // 1.21.1<
                profileRetriever = meta -> {
                    try {
                        field.setAccessible(true);
                        return ((GameProfile) field.get(meta));
                    } catch (IllegalAccessException exception) {
                        exception.printStackTrace();
                        return null;
                    }
                };
            } else if (field.getType().getSimpleName().contains("Profile" /* ResolvableProfile */)) {
                // 1.12.1>
                Class<?> resolvableProfileClazz = field.getType();
                for (Method method : resolvableProfileClazz.getMethods()) {
                    if (method.getReturnType() == GameProfile.class) {
                        profileRetriever = meta -> {
                            field.setAccessible(true);
                            try {
                                return ((GameProfile) method.invoke(field.get(meta)));
                            } catch (IllegalAccessException | InvocationTargetException exception) {
                                exception.printStackTrace();
                                return null;
                            }
                        };
                        break;
                    }
                }
            } else {
                continue;
            }
            break;
        }

        if (profileRetriever == null) {
            throw new RuntimeException("Was not able to find GameProfile retriever");
        }
        final Function<SkullMeta, GameProfile> retriever = profileRetriever;
        UUID_RETRIEVER = meta -> {
            GameProfile profile = retriever.apply(meta);
            return profile == null ? null : profile.getId();
        };
    }

    private final IMat materialFactory;
    private final Material tinted;
    @Getter
    private final ItemTag itemTagAdapter;

    public VersionControlFactory(LogChannel logChannel) {
        MinecraftVersion.NMSVersion nmsVersion = MinecraftVersion.getPossibleNMSVersion();
        logChannel.info("Loading NMS for your platform ("
                + Bukkit.getVersion() + ", " + nmsVersion.getCraftBukkitName() + ")...");
        // Load NMS classes
        switch (nmsVersion) {
            case v1_21_R1: {
                itemTagAdapter = new ItemTag1_21_R1();
                break;
            }
            case v1_20_R4: {
                itemTagAdapter = new ItemTag1_20_R4();
                break;
            }
            case v1_20_R3: {
                itemTagAdapter = new ItemTag1_20_R3();
                break;
            }
            case v1_20_R2: {
                itemTagAdapter = new ItemTag1_20_R2();
                break;
            }
            case v1_20_R1: {
                itemTagAdapter = new ItemTag1_20_R1();
                break;
            }
            case v1_19_R3: {
                itemTagAdapter = new ItemTag1_19_R3();
                break;
            }
            case v1_19_R2: {
                itemTagAdapter = new ItemTag1_19_R2();
                break;
            }
            case v1_19_R1: {
                itemTagAdapter = new ItemTag1_19_R1();
                break;
            }
            case v1_18_R2: {
                itemTagAdapter = new ItemTag1_18_R2();
                break;
            }
            case v1_18_R1: {
                itemTagAdapter = new ItemTag1_18_R1();
                break;
            }
            default: {
                try {
                    itemTagAdapter = new ItemTagLegacy();
                    break;
                } catch (UnsupportedOperationException ex) {
                    throw new RuntimeException("Platform is not supported");
                }
            }
        }

        // Load material adapters
        // Material
        materialFactory = JavaUtils.getEnum(Material.class, "PLAYER_HEAD") == null
                ? new Mat1_12() : new Mat1_13();
        logChannel.info("Loaded " + materialFactory.build() + " material version");

        this.tinted = JavaUtils.getEnum(Material.class, "TINTED_GLASS");
    }

    @Override
    public String getNmsVersion() {
        return MinecraftVersion.getPossibleNMSVersion().getCraftBukkitName();
    }

    public IMat getMat() {
        return materialFactory;
    }

    @Override
    public boolean isModern() {
        return materialFactory instanceof Mat1_13;
    }

    @Override
    public boolean isLegacy() {
        return materialFactory instanceof Mat1_12;
    }

    public Material getTintedMaterial() {
        return tinted;
    }

    @Override
    public ItemStack apply(ItemStack origin, Identifier identifier) {
        return apply(origin, identifier.getTagName(), identifier.getTagValue());
    }

    @Override
    public ItemStack apply(ItemStack origin, String tagName, String tagValue) {
        Object nmsItem = itemTagAdapter.asNMSCopy(origin);
        Object tag = itemTagAdapter.getTag(nmsItem);
        if (tag == null) {
            tag = itemTagAdapter.newTag();
        }
        itemTagAdapter.setTagString(tag, tagName, tagValue);
        itemTagAdapter.setTag(nmsItem, tag);
        return itemTagAdapter.asBukkitCopy(nmsItem);
    }

    @Override
    public String getValue(ItemStack origin, Identifier identifier) {
        return getValue(origin, identifier.getTagValue());
    }

    @Override
    public String getValue(ItemStack item, String tagName) {
        final Object tag = itemTagAdapter.getTag(itemTagAdapter.asNMSCopy(item));
        if (tag == null) {
            return null;
        }

        final String id = itemTagAdapter.getTagString(tag, tagName);
        return id != null && id.isEmpty() ? null : id;
    }

    @Override
    public ItemStack getPlayerHead(String url, String name, List<String> lore, UUID uuid) {
        if (url == null || url.isEmpty()) {
            throw new UnsupportedOperationException("URL cannot be null");
        }
        if (!url.contains("textures.minecraft.net/texture")) {
            url = "http://textures.minecraft.net/texture/" + url;
        }

        ItemStack head = this.materialFactory.getItem(IMat.Mat.PLAYER_SKULL, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;

        GameProfile profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid, PROFILE_NAME);
        profile.getProperties().put("textures", new Property("textures",
                new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()))));

        try {
            // New method that modifies profile and serializedProfile
            Method method = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            method.setAccessible(true);
            method.invoke(headMeta, profile);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex1) {
            try {
                // MC 1.21.1 +
                Class<?> clazz = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
                Method method = headMeta.getClass().getDeclaredMethod("setProfile", clazz);
                Object resolvable = clazz.getConstructor(GameProfile.class).newInstance(profile);
                method.setAccessible(true);
                method.invoke(headMeta, resolvable);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                     | InvocationTargetException | IllegalAccessException ex2) {
                try {
                    // Old method that modifies profile
                    Field profileField = headMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(headMeta, profile);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex3) {
                    ex1.printStackTrace();
                    ex2.printStackTrace();
                    ex3.printStackTrace();
                }
            }
        }

        headMeta.setDisplayName(name);
        headMeta.setLore(lore);

        head.setItemMeta(headMeta);
        return head;
    }

    public Object asNMSCopy(ItemStack itemStack) {
        return this.itemTagAdapter.asNMSCopy(itemStack);
    }

    public UUID getUUID(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof SkullMeta)) {
            return null;
        }
        return UUID_RETRIEVER.apply((SkullMeta) meta);
    }
}
