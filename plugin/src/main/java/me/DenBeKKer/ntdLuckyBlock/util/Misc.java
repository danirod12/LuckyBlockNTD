package me.DenBeKKer.ntdLuckyBlock.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.string.PopulationResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

    private final static String PROFILE_NAME;
    private final static Pattern LOCATION_PATTERN = Pattern.compile("%(?:|(?<target>player|block)_)location" +
            "(?:|_above_(?<above>(?:|-)[0-9]+(?:|.[0-9]+)))(?<int>|_int)%", Pattern.CASE_INSENSITIVE);

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

    private static PopulationResult populateWithLocation(String origin, Block block, Player player) {
        Matcher matcher = LOCATION_PATTERN.matcher(origin);

        PopulationResult.ValidationLevel validationLevel = PopulationResult.ValidationLevel.SUCCESS;
        boolean touchesOriginBlock = false;
        int lastIndex = 0;
        StringBuilder result = new StringBuilder(origin.length() + 16);
        while (matcher.find()) {
            result.append(origin, lastIndex, matcher.start());

            Location location = null;
            if (Objects.equals(matcher.group("target"), "block")) {
                if (block == null) {
                    validationLevel = validationLevel.negate(PopulationResult.ValidationLevel.NO_BLOCK);
                } else {
                    location = block.getLocation().add(.5, .5, .5);
                }
            } else {
                if (player == null) {
                    validationLevel = validationLevel.negate(PopulationResult.ValidationLevel.NO_PLAYER);
                } else {
                    location = player.getLocation().clone();
                }
            }

            if (location == null) {
                lastIndex = matcher.start();
                continue;
            }

            try {
                double additionY = Double.parseDouble(matcher.group("above"));
                location.setY(location.getY() + additionY);
            } catch (NullPointerException | NumberFormatException ignored) {
            }

            if (block != null && block.getX() == location.getBlockX()
                    && block.getY() == location.getBlockY() && block.getZ() == location.getBlockZ()) {
                touchesOriginBlock = true;
            }

            if (matcher.group("int").isEmpty()) {
                result.append(location.getX()).append(" ")
                        .append(location.getY()).append(" ").append(location.getZ());
            } else {
                result.append(location.getBlockX()).append(" ")
                        .append(location.getBlockY()).append(" ").append(location.getBlockZ());
            }
            lastIndex = matcher.end();
        }

        return new PopulationResult(
                result.append(origin, lastIndex, origin.length()).toString(), validationLevel, touchesOriginBlock
        );
    }

    public static void performCommand(String command, Block block, Player player, PerformCommandAs as) {
        PopulationResult populationResult = Misc.populateWithLocation(
                command.replace("%world%", block.getWorld().getName()), block, player);
        if (populationResult.getValidationLevel() == PopulationResult.ValidationLevel.SUCCESS) {
            String result = populationResult.getResult();
            CommandSender commandSender = null;
            if (player != null) {
                result = result.replace("%player%", player.getName());
                if (as != PerformCommandAs.CONSOLE) {
                    commandSender = player;
                }
            }

            if (commandSender == null) {
                commandSender = Bukkit.getConsoleSender();
            }

            if (populationResult.isTouchesOriginBlock()) {
                final String finalResult = result;
                final CommandSender finalCommandSender = commandSender;
                Bukkit.getScheduler().runTask(LBMain.getInstance(), () ->
                        dispatch(finalCommandSender, finalResult, as == PerformCommandAs.OPPED_PLAYER));
            } else {
                dispatch(commandSender, result, as == PerformCommandAs.OPPED_PLAYER);
            }
        }
    }

    public static void dispatch(CommandSender sender, String command, boolean opped) {
        boolean wasOpped = false;

        if (opped) {
            wasOpped = sender.isOp();
            sender.setOp(true);
        }

        // performCommand should not throw an exception, but we still checks for any throwable to be sure to close
        // player access for op. Otherwise, if by chance our code fail, player will stay opped hacking your server
        try {
            Bukkit.dispatchCommand(sender, command);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (opped) {
                sender.setOp(wasOpped);
            }
        }
    }

    public enum PerformCommandAs {
        CONSOLE,
        PLAYER,
        OPPED_PLAYER
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
            try {
                // New method that modifies profile and serializedProfile
                Method method = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                method.setAccessible(true);
                method.invoke(headMeta, profile);
            } catch (NoSuchMethodException | InvocationTargetException exception) {
                // Old method that modifies profile
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            }
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
