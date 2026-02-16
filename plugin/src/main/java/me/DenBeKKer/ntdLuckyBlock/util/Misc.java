package me.DenBeKKer.ntdLuckyBlock.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Misc {

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

    public static int parseInteger(String base, int min) {
        try {
            return Math.max(min, Integer.parseInt(base));
        } catch (NumberFormatException exception) {
            return min;
        }
    }

    public static void throwExplosion(Entity... entities) {
        for (Entity entity : entities) {
            entity.setVelocity(new Vector(ThreadLocalRandom.current().nextDouble(-.25, .25),
                    ThreadLocalRandom.current().nextDouble(.75, 1.25),
                    ThreadLocalRandom.current().nextDouble(-.25, .25)));
        }
    }

    public static Entity[] spawnEntities(Location location, EntityType type, int amount) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException();
        }
        Entity[] entities = new Entity[amount];
        for (int i = 0; i < amount; i++) {
            entities[i] = world.spawnEntity(location, type);
        }
        return entities;
    }
}
