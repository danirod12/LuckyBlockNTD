package me.DenBeKKer.ntdLuckyBlock.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Misc {

    private final static String PROFILE_NAME;

    static {
        String profileName;
        try {
            // Causes NullPointerException since 1.20.2
            new GameProfile(UUID.randomUUID(), null);
            profileName = null;
        } catch (NullPointerException exception) {
            profileName = "md_5";
        }
        PROFILE_NAME = profileName;
    }

    /**
     * @param target Player to check permission
     * @param node   Permission node
     * @return Does player have permission
     * @throws UnsupportedOperationException Permission node or player is null
     */
    public static boolean hasPermission(final Player target, String node) throws UnsupportedOperationException {
        if (node == null || target == null) {
            throw new UnsupportedOperationException();
        }

        node = node.toLowerCase();
        if (target.hasPermission(node))
            return true;

        String permission = null;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; ++i) {
            permission = permission == null ? nodes[i] : permission + "." + nodes[i];
            if (target.hasPermission(permission + ".*"))
                return true;
        }
        return false;
    }

    /**
     * Replace "&#rrggbbTest" to "§x§r§r§g§g§b§bTest"
     *
     * @param origin String to be filled with colors
     * @return Colored minecraft string
     */
    public static String setColors(String origin) {
        return origin.replace("&", "§").replaceAll("§#([a-fA-F\\d])"
                + "([a-fA-F\\d])([a-fA-F\\d])([a-fA-F\\d])([a-fA-F\\d])([a-fA-F\\d])", "§x§$1§$2§$3§$4§$5§$6");
    }

    /**
     * @param array Array to be stringed
     * @param a     Always insert brackets
     * @return Stringed array
     */
    public static String toString(List<?> array, boolean a) {

        if (array.size() == 0) return a ? "[]" : "";
        if (array.size() == 1) return (a ? "[]" : "") + array.get(0) + (a ? "[]" : "");

        String string = null;
        for (Object obj : array)
            string = string == null ? obj.toString() : string + ", " + obj.toString();
        return "[" + string + "]";

    }

    public static String getLocation(Location location) {
        return location.getX() + " " + location.getY() + " " + location.getZ();
    }

    @Deprecated
    public static String getLocation(Player target) {
        return getLocation(target.getLocation());
    }

    public static Class<?> getClass(String path) {
        try {
            return Class.forName(path);
        } catch (Throwable th) {
            return null;
        }
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (Exception exception) {
            return null;
        }
    }

    public static ChatColor getColorLevel(long number, int yellow, int red) {
        if (number > red) return ChatColor.RED;
        if (number > yellow) return ChatColor.YELLOW;
        return ChatColor.GREEN;
    }

    public static UUID getUUID(ItemStack item) {

        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta)) return null;

        SkullMeta skullMeta = (SkullMeta) meta;
        try {

            Field profileField = skullMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);

            GameProfile profile = (GameProfile) profileField.get(skullMeta);
            return profile == null ? null : profile.getId();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ItemStack getPlayerHead(String url, String name, List<String> lore, UUID uuid) {

        if (url == null || url.isEmpty())
            throw new UnsupportedOperationException("URL cannot be null");

        ItemStack head = LBMain.getInstance().factory.getItem(IMat.Mat.PLAYER_SKULL, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;

        GameProfile profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid, PROFILE_NAME);
        profile.getProperties().put("textures", new Property("textures",
                new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()))));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        headMeta.setDisplayName(name);
        headMeta.setLore(lore);

        head.setItemMeta(headMeta);
        return head;

    }

    public static void giveItemsOrDrop(Player target, ItemStack... itemStacks) {
        PlayerInventory playerInventory = target.getInventory();
        for (ItemStack value : playerInventory.addItem(itemStacks).values()) {
            target.getWorld().dropItem(target.getLocation(), value);
        }
    }
}
