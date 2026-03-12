package me.DenBeKKer.ntdLuckyBlock.api.model;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

@Getter
public class Identifier {

    private static final Pattern PATTERN = Pattern.compile("[a-z][a-z0-9_]{2,}[a-z0-9]");

    private final String identifier, tagName;

    /**
     * Custom tags with plugin
     *
     * @param plugin     Plugin instance
     * @param identifier Identifier
     * @param tagName    Tag key
     */
    public Identifier(Plugin plugin, String identifier, String tagName) {
        if (!PATTERN.matcher(identifier).matches()) {
            throw new UnsupportedOperationException("Identifier should be from pattern \"" + PATTERN.pattern() + "\"");
        }

        this.identifier = plugin.getName().toLowerCase() + "-" + identifier.toLowerCase();
        this.tagName = tagName.toLowerCase();
    }

    /**
     * Custom tags without plugin
     *
     * @param identifier Identifier
     * @param tagName    Tag key
     */
    public Identifier(String identifier, String tagName) {
        this.identifier = identifier.toLowerCase();
        this.tagName = tagName.toLowerCase();
    }

    public ItemStack apply(ItemStack origin) {
        return LuckyBlockAPI.insertTag(origin, this);
    }

    @Override
    public String toString() {
        return identifier + ":" + tagName;
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
        return LuckyBlockAPI.checkTag(stack, this, null);
    }

    public boolean isItem(Identifier identifier) {
        return identifier.identifier.equalsIgnoreCase(this.identifier);
    }

    public boolean isItem(String string) {
        return string.equalsIgnoreCase(identifier);
    }
}
