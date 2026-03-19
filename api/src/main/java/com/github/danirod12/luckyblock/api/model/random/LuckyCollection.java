package com.github.danirod12.luckyblock.api.model.random;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface LuckyCollection<T> extends WeightCollection<T> {
    Amount getAmount();

    void setAmount(@NotNull Amount amount);

    String getPermission();

    void setPermission(String permission);

    boolean hasPermission(Player player);

    Set<T> getAll();
}
