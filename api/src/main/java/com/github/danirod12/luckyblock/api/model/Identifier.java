package com.github.danirod12.luckyblock.api.model;

import de.tr7zw.nbtapi.NBT;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

/**
 * A solution to set plugin-name:tag=value PDC value for all versions even before 1.14,
 * which is not supported by Bukkit API.
 */
@Getter
public class Identifier {

    private static final Pattern PATTERN = Pattern.compile("[a-z][a-z0-9_]{2,}[a-z0-9]");

    private final String tagName;
    private final String tagValue;

    /**
     * Creates an Identifier with the specified tag name and tag value.
     * The tag value is prefixed with the plugin name to ensure uniqueness across different plugins.
     *
     * @param tagName  the name of the tag to be used in the item's NBT data
     * @param plugin   the plugin whose name will be used as a prefix for the tag value
     * @param tagValue the value of the tag, which will be combined with the plugin name to create a unique identifier
     * @throws UnsupportedOperationException if the tag value does not match the required pattern
     */
    public Identifier(String tagName, Plugin plugin, String tagValue) {
        if (!PATTERN.matcher(tagValue).matches()) {
            throw new UnsupportedOperationException("Identifier should be from pattern \"" + PATTERN.pattern() + "\"");
        }

        this.tagName = tagName.toLowerCase();
        this.tagValue = plugin.getName().toLowerCase() + "-" + tagValue.toLowerCase();
    }

    /**
     * Creates an Identifier with the specified tag name and tag value.
     * This constructor does not prefix the tag value with a plugin name,
     * so it should be used with caution to avoid conflicts between different plugins.
     *
     * @param tagName  the name of the tag to be used in the item's NBT data
     * @param tagValue the value of the tag, which should be unique across all plugins to avoid conflicts
     * @throws UnsupportedOperationException if the tag value does not match the required pattern
     */
    public Identifier(String tagName, String tagValue) {
        this.tagName = tagName.toLowerCase();
        this.tagValue = tagValue.toLowerCase();
    }

    /**
     * Applies this Identifier to the given ItemStack by setting the specified tag name
     * and tag value in the item's NBT data.
     *
     * @param origin the ItemStack to which this Identifier will be applied
     * @return the modified ItemStack with the Identifier applied
     */
    public ItemStack apply(ItemStack origin) {
        NBT.modify(origin, nbt -> {
            nbt.setString(this.tagName, this.tagValue);
        });
        return origin;
    }

    /**
     * Returns a string representation of this Identifier in the format "tagValue:tagName".
     *
     * @return a string representation of this Identifier
     */
    @Override
    public String toString() {
        return tagValue + ":" + tagName;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof String) {
            return isItem((String) object);
        }
        if (object instanceof Identifier) {
            return isItem((Identifier) object);
        }
        if (object instanceof ItemStack) {
            return isItem((ItemStack) object);
        }
        return false;
    }

    /**
     * Checks if the given ItemStack has this Identifier applied by comparing the tag value in the item's NBT data
     * with this Identifier's tag value.
     *
     * @param stack the ItemStack to check for this Identifier
     * @return true if the ItemStack has this Identifier applied, false otherwise
     */
    public boolean isItem(ItemStack stack) {
        String foundValue = NBT.get(stack, nbt -> {
            return nbt.getString(this.tagName);
        });
        return this.tagValue.equalsIgnoreCase(foundValue);
    }

    /**
     * Checks if the given Identifier has the same tag value as this Identifier.
     *
     * @param identifier the Identifier to compare with this Identifier
     * @return true if the given Identifier has the same tag value, false otherwise
     */
    public boolean isItem(Identifier identifier) {
        return identifier.tagValue.equalsIgnoreCase(this.tagValue);
    }

    /**
     * Checks if the given string matches this Identifier's tag value.
     *
     * @param string the string to compare with this Identifier's tag value
     * @return true if the given string matches this Identifier's tag value, false otherwise
     */
    public boolean isItem(String string) {
        return string.equalsIgnoreCase(tagValue);
    }
}
