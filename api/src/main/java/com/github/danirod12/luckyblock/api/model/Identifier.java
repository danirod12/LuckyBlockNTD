package com.github.danirod12.luckyblock.api.model;

import de.tr7zw.nbtapi.NBT;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

@Getter
public class Identifier {

    private static final Pattern PATTERN = Pattern.compile("[a-z][a-z0-9_]{2,}[a-z0-9]");

    private final String tagName, tagValue;

    public Identifier(String tagName, Plugin plugin, String tagValue) {
        if (!PATTERN.matcher(tagValue).matches()) {
            throw new UnsupportedOperationException("Identifier should be from pattern \"" + PATTERN.pattern() + "\"");
        }

        this.tagName = tagName.toLowerCase();
        this.tagValue = plugin.getName().toLowerCase() + "-" + tagValue.toLowerCase();
    }

    public Identifier(String tagName, String tagValue) {
        this.tagName = tagName.toLowerCase();
        this.tagValue = tagValue.toLowerCase();
    }

    public ItemStack apply(ItemStack origin) {
        NBT.modify(origin, nbt -> {
            nbt.setString(this.tagName, this.tagValue);
        });
        return origin;
    }

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

    public boolean isItem(ItemStack stack) {
        String foundValue = NBT.get(stack, nbt -> {
            return nbt.getString(this.tagName);
        });
        return this.tagValue.equalsIgnoreCase(foundValue);
    }

    public boolean isItem(Identifier identifier) {
        return identifier.tagValue.equalsIgnoreCase(this.tagValue);
    }

    public boolean isItem(String string) {
        return string.equalsIgnoreCase(tagValue);
    }
}
