package me.DenBeKKer.ntdLuckyBlock.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.util.string.PopulationResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

    private final static String PROFILE_NAME;
    private final static Pattern LOCATION_PATTERN = Pattern.compile(
            "%(?:|(?<target>player|block)_)" +
                    "(?<format>location|x|y|z)" +
                    "(?:|_above_(?<above>(?:|-)[0-9]+(?:|.[0-9]+)))" +
                    "(?:|_move_\\((?<move>[0-9\\-.,]*)\\))" +
                    "(?<int>|_int)%",
            Pattern.CASE_INSENSITIVE
    );
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
        UUID_RETRIEVER = meta -> {
            GameProfile profile = retriever.apply(meta);
            return profile == null ? null : profile.getId();
        };
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
                location.setY(location.getY() + Double.parseDouble(matcher.group("above")));
            } catch (NullPointerException | NumberFormatException ignored) {
            }

            String move = matcher.group("move");
            try {
                if (move != null) {
                    String[] offset = move.split(",");
                    location.add(
                            Double.parseDouble(offset[0]),
                            Double.parseDouble(offset[1]),
                            Double.parseDouble(offset[2])
                    );
                }
            } catch (NumberFormatException | IndexOutOfBoundsException exception) {
                MvLogger.log(Level.WARNING, "An error occurred while parsing placeholder "
                        + origin.substring(matcher.start(), matcher.end()) + " at -> move_" + move);
                exception.printStackTrace();
            }

            if (block != null && block.getX() == location.getBlockX()
                    && block.getY() == location.getBlockY() && block.getZ() == location.getBlockZ()) {
                touchesOriginBlock = true;
            }
            appendLocation(result, location,
                    LocationFormat.parse(matcher.group("format")), !matcher.group("int").isEmpty());
            lastIndex = matcher.end();
        }

        return new PopulationResult(
                result.append(origin, lastIndex, origin.length()).toString(), validationLevel, touchesOriginBlock
        );
    }

    private static void appendLocation(StringBuilder target, Location location,
                                       LocationFormat format, boolean asInteger) {
        if (format.hasX()) {
            if (asInteger) {
                target.append(location.getBlockX());
            } else {
                target.append(cut(location.getX()));
            }
            if (format == LocationFormat.EVERYTHING) {
                target.append(' ');
            }
        }
        if (format.hasY()) {
            if (asInteger) {
                target.append(location.getBlockY());
            } else {
                target.append(cut(location.getY()));
            }
            if (format == LocationFormat.EVERYTHING) {
                target.append(' ');
            }
        }
        if (format.hasZ()) {
            if (asInteger) {
                target.append(location.getBlockZ());
            } else {
                target.append(cut(location.getZ()));
            }
        }
    }

    public static double cut(double origin) {
        return ((long) (origin * 100)) / 100D;
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

    public enum LocationFormat {
        X, Y, Z, EVERYTHING;

        public static LocationFormat parse(String mode) {
            switch (mode.toLowerCase()) {
                case "x":
                    return X;
                case "y":
                    return Y;
                case "z":
                    return Z;
                default:
                    return EVERYTHING;
            }
        }

        public boolean hasX() {
            return this == X || this == EVERYTHING;
        }

        public boolean hasY() {
            return this == Y || this == EVERYTHING;
        }

        public boolean hasZ() {
            return this == Z || this == EVERYTHING;
        }
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

        if (url == null || url.isEmpty())
            throw new UnsupportedOperationException("URL cannot be null");
        if (!url.contains("textures.minecraft.net/texture")) {
            url = "http://textures.minecraft.net/texture/" + url;
        }

        ItemStack head = LBMain.getInstance().factory.getItem(IMat.Mat.PLAYER_SKULL, 1);
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

    public static void giveItemsOrDrop(Player target, ItemStack... itemStacks) {
        PlayerInventory playerInventory = target.getInventory();
        for (ItemStack value : playerInventory.addItem(itemStacks).values()) {
            target.getWorld().dropItem(target.getLocation(), value);
        }
    }
}
