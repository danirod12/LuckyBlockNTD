package com.github.danirod12.luckyblock.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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


/**
 * @author danirod12 - Den Bekker
 *
 * Universal class for player heads handling
 */
public class PlayerHeadUtils {
    private static final String PROFILE_NAME;
    private static final ItemStack SKULL_EXAMPLE;
    private static final Function<SkullMeta, UUID> UUID_RETRIEVER;

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

        ItemStack skullExample;
        try {
            // 1.13+
            skullExample = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException exception) {
            // legacy method
            skullExample = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        SKULL_EXAMPLE = skullExample;
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

    public PlayerHeadUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static ItemStack getPlayerHead(String minecraftServiceURL, String name, List<String> lore, UUID uuid) {
        if (minecraftServiceURL == null || minecraftServiceURL.isEmpty()) {
            throw new UnsupportedOperationException("URL cannot be null");
        }
        String url = minecraftServiceURL;
        if (!url.contains("textures.minecraft.net/texture")) {
            url = "http://textures.minecraft.net/texture/" + url;
        }

        ItemStack head = SKULL_EXAMPLE.clone();
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

    public static UUID getUUID(ItemStack stack) {
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
