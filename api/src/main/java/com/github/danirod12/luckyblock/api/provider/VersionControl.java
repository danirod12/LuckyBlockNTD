package com.github.danirod12.luckyblock.api.provider;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface VersionControl {
    String getNmsVersion();

    boolean isModern();

    boolean isLegacy();

    ItemStack getPlayerHead(String url, String name, List<String> lore, UUID uuid);
}
