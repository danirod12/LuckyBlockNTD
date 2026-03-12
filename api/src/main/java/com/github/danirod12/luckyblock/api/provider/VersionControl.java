package com.github.danirod12.luckyblock.api.provider;

import com.github.danirod12.luckyblock.api.model.Identifier;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface VersionControl {
    String getNmsVersion();

    boolean isModern();

    boolean isLegacy();

    ItemStack apply(ItemStack origin, Identifier identifier);

    ItemStack apply(ItemStack origin, String tagName, String identifier);

    String getValue(ItemStack item, Identifier identifier);

    String getValue(ItemStack item, String tagName);

    ItemStack getPlayerHead(String url, String name, List<String> lore, UUID uuid);
}
