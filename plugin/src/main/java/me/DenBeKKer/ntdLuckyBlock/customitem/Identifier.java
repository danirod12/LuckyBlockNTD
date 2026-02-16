package me.DenBeKKer.ntdLuckyBlock.customitem;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.nms.ItemTag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

public class Identifier {

    private static final Pattern pattern = Pattern.compile("[a-z][a-z0-9_]{2,}[a-z0-9]");

    private final String identifier, tagName;

    /**
     * Custom tags with plugin
     */
    public Identifier(Plugin plugin, String tagName, String identifier) {
        if (!pattern.matcher(identifier).matches()) {
            throw new UnsupportedOperationException("Identifier should be from pattern \"" + pattern.pattern() + "\"");
        }

        this.identifier = plugin.getName().toLowerCase() + "-" + identifier.toLowerCase();
        this.tagName = tagName.toLowerCase();
    }

    /**
     * Custom items
     */
    public Identifier(Plugin plugin, String identifier) {
        this(plugin, CustomItemFactory.TAG_IDENTIFIER_NAME, identifier);
    }

    /**
     * Custom tags without plugin
     */
    public Identifier(String tagName, String identifier) {
        this.identifier = identifier.toLowerCase();
        this.tagName = tagName.toLowerCase();
    }

    public ItemStack apply(ItemStack origin) {
        ItemTag adapter = LBMain.getInstance().getItemTagAdapter();
        Object nmsItem = adapter.asNMSCopy(origin);
        Object tag = adapter.getTag(nmsItem);
        if (tag == null) tag = adapter.newTag();
        adapter.setTagString(tag, tagName, identifier);
        adapter.setTag(nmsItem, tag);
        return adapter.asBukkitCopy(nmsItem);
    }

    @Override
    public String toString() {
        return identifier + ":" + tagName;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Identifier && object.toString().equals(toString());
    }

    public String getTagName() {
        return tagName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
