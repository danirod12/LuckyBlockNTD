package com.github.danirod12.luckyblock.api.model.random;

import java.util.Collection;
import java.util.Map;

public interface WeightCollection<T> extends Collection<T> {
    void add(T element, double weight);

    Map<T, Double> toWeightMap();
}
