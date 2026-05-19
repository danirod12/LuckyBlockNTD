package com.github.danirod12.luckyblock.util.random;

import com.github.danirod12.luckyblock.api.util.Pair;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class WeightListBuilder<T, B> {
    private final List<WeightPair> list = new ArrayList<>();
    private final List<Pair<T, B>> auto = new ArrayList<>();

    /**
     * Adds an element to the list with the specified weight.
     *
     * @param type   the element to add
     * @param weight the weight of the element, if 0 or less, it will be added to the auto list
     * @param bind   the bind of the element, can be null
     */
    public void add(T type, double weight, B bind) {
        if (weight <= 0) {
            this.addAuto(type, bind);
            return;
        }
        list.add(new WeightPair(type, weight, bind));
    }

    public void addAuto(T type, B bind) {
        auto.add(new Pair<>(type, bind));
    }

    public void append(WeightListB<T, B> weightList) {
        for (WeightPair pair : list) {
            weightList.add(pair.type, pair.weight, pair.bind);
        }

        double weight = weightList.isEmpty() ? 50 : Math.max(0.000001, weightList.getTotalWeight() / weightList.size());
        for (Pair<T, B> type : auto) {
            weightList.add(type.getA(), weight, type.getB());
        }
    }

    @AllArgsConstructor
    private class WeightPair {
        public final T type;
        public final double weight;
        public final B bind;
    }
}
