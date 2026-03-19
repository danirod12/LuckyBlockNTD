package com.github.danirod12.luckyblock.api.model.random;

import java.util.Collection;

public interface WeightCollection<T> extends Collection<T> {
    void add(T element, double weight);
}
