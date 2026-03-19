package com.github.danirod12.luckyblock.util.random;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class WeightListBuilder<T> {
    private final List<WeightPair> list = new ArrayList<>();
    private final List<T> auto = new ArrayList<>();

    public void add(T type, int weight) {
        list.add(new WeightPair(type, weight));
    }

    public void addAuto(T type) {
        auto.add(type);
    }

    public WeightList<T> build() {
        return append(new WeightList<>());
    }

    public WeightList<T> append(WeightList<T> weightList) {
        for (WeightPair pair : list) {
            weightList.add(pair.type, pair.weight);
        }

        double weight = weightList.getTotalWeight() / weightList.size();
        for (T type : auto) {
            weightList.add(type, weight);
        }
        return weightList;
    }

    @AllArgsConstructor
    private class WeightPair {
        public final T type;
        public final int weight;
    }
}
