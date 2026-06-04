package com.github.danirod12.luckyblock.api.model.random;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents a collection of items that can be rolled with a certain chance.
 * The amount of items rolled is determined by the {@link Amount} associated with this collection.
 *
 * @param <T> the type of items in this collection
 */
public interface LuckyCollection<T> extends WeightCollection<T> {
    /**
     * Gets the amount of items to roll from this collection.
     *
     * @return the amount of items to roll
     */
    Amount getAmount();

    /**
     * Sets the amount of items to roll from this collection.
     *
     * @param amount the new amount of items to roll
     */
    void setAmount(@NotNull Amount amount);

    /**
     * Rolls items from this collection based on the defined amount and chance.
     *
     * @param player the player for whom the items are being rolled
     * @return a set of rolled items
     */
    Set<T> rollItems(Player player);
}
