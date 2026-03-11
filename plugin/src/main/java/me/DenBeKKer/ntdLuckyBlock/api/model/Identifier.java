package me.DenBeKKer.ntdLuckyBlock.api.model;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
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
     * @param tagName    Tag key
     * @param identifier Identifier
     */
    public Identifier(Plugin plugin, String tagName, String identifier) {
        if (!PATTERN.matcher(identifier).matches()) {
            throw new UnsupportedOperationException("Identifier should be from pattern \"" + PATTERN.pattern() + "\"");
        }

        this.identifier = plugin.getName().toLowerCase() + "-" + identifier.toLowerCase();
        this.tagName = tagName.toLowerCase();
    }

    /**
     * Custom items
     *
     * @param plugin     Plugin instance
     * @param identifier Identifier
     */
    public Identifier(Plugin plugin, String identifier) {
        this(plugin, CustomItemFactory.TAG_IDENTIFIER_NAME, identifier);
    }

    /**
     * Custom tags without plugin
     *
     * @param tagName    Tag key
     * @param identifier Identifier
     */
    public Identifier(String tagName, String identifier) {
        this.identifier = identifier.toLowerCase();
        this.tagName = tagName.toLowerCase();
    }

    @Deprecated
    public ItemStack apply(ItemStack origin) {
        return LuckyBlockAPI.getLuckyEngineProvider().getVersionControl().apply(origin, tagName, identifier);
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
            return compare((String) object);
        }
        if (object instanceof Identifier) {
            return compare((Identifier) object);
        }
        if (object instanceof ItemStack) {
            return compare((ItemStack) object);
        }
        return false;
    }

    public boolean compare(ItemStack stack) {
        return CustomItemFactory.compare(stack, tagName, identifier);
    }

    public boolean compare(Identifier identifier) {
        return identifier.identifier.equalsIgnoreCase(this.identifier);
    }

    public boolean compare(String string) {
        return string.equalsIgnoreCase(identifier);
    }
}
