package com.github.danirod12.luckyblock.api.model.random;

import java.util.Collection;
import java.util.Map;

/**
 * A collection that allows adding elements with associated weights.
 *
 * @param <T> the type of elements in the collection
 */
public interface WeightCollection<T> extends Collection<T> {
    /**
     * Adds an element to the collection with a specified weight.
     *
     * @param element the element to add
     * @param weight  the weight associated with the element
     */
    void add(T element, double weight);

    /**
     * Retrieves a map of elements and their associated weights.
     *
     * @return a map where keys are elements and values are their weights
     */
    Map<T, Double> toWeightMap();
}
