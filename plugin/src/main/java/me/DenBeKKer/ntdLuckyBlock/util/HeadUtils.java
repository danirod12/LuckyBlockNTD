package me.DenBeKKer.ntdLuckyBlock.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
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
 * @author danirod12
 * Support for player head texture insertion 1.8 - 1.21.9
 */
public class HeadUtils {
    private static final String PROFILE_NAME;
    private static final ItemStack PLAYER_HEAD_EXAMPLE;
    private static final Function<SkullMeta, UUID> UUID_RETRIEVER;
    private static final boolean PROFILE_PROPERTY_MAP_INIT;

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

        ItemStack playerHeadExample;
        try {
            playerHeadExample = new ItemStack(Material.PLAYER_HEAD);
        } catch (Throwable throwable) {
            playerHeadExample = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        PLAYER_HEAD_EXAMPLE = playerHeadExample;

        Class<?> skullMetaClazz = playerHeadExample.getItemMeta().getClass();

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
                                Object object = field.get(meta);
                                return object == null ? null : ((GameProfile) method.invoke(object));
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

        // unmodifiable also since 1.21.9
        boolean unmodifiablePropertyMap = true;
        try {
            // record since 1.21.9
            GameProfile.class.getMethod("getId");
            unmodifiablePropertyMap = false;
        } catch (Exception ignored) {
        }
        PROFILE_PROPERTY_MAP_INIT = unmodifiablePropertyMap;

        UUID_RETRIEVER = meta -> {
            GameProfile profile = retriever.apply(meta);
            if (profile == null) {
                return null;
            }

            if (PROFILE_PROPERTY_MAP_INIT) {
                return profile.id();
            } else {
                try {
                    return (UUID) GameProfile.class.getMethod("getId").invoke(profile);
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        };
    }

    public static UUID getUUID(ItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta)) {
            return null;
        }
        return UUID_RETRIEVER.apply((SkullMeta) meta);
    }

    public static ItemStack getPlayerHead(String url, String name, List<String> lore, UUID uuid) {
        if (url == null || url.isEmpty()) {
            throw new UnsupportedOperationException("URL cannot be null");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://textures.minecraft.net/texture/" + url;
        }

        ItemStack head = PLAYER_HEAD_EXAMPLE.clone();
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        Property property = new Property("textures",
                new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes())));

        GameProfile profile;

        if (PROFILE_PROPERTY_MAP_INIT) {
            // 1.21.9+
            Multimap<String, Property> multimap = HashMultimap.create();
            multimap.put("textures", property);
            profile = new GameProfile(uuid, PROFILE_NAME, new PropertyMap(multimap));
        } else {
            // < 1.21.9
            profile = new GameProfile(uuid, PROFILE_NAME);
            try {
                Method method = GameProfile.class.getMethod("getProperties");
                PropertyMap propertyMap = (PropertyMap) method.invoke(profile);
                propertyMap.put("textures", property);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        try {
            // New method that modifies profile and serializedProfile
            Method method = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            method.setAccessible(true);
            method.invoke(headMeta, profile);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex1) {
            try {
                Class<?> clazz = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
                Object resolvable;
                try {
                    // MC 1.21.9 +
                    resolvable = new net.minecraft.world.item.component.ResolvableProfile.Static(Either.left(profile),
                            net.minecraft.world.entity.player.PlayerSkin.Patch.a);
                } catch (Throwable throwable) {
                    // MC 1.21.1 +
                    resolvable = clazz.getConstructor(GameProfile.class).newInstance(profile);
                }
                Method method = headMeta.getClass().getDeclaredMethod("setProfile", clazz);
                method.setAccessible(true);
                method.invoke(headMeta, resolvable);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                     InvocationTargetException | IllegalAccessException ex2) {
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
}
