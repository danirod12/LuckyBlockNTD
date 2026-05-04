package com.github.danirod12.luckyblock.util.random;

import com.github.danirod12.luckyblock.api.model.random.WeightCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WeightListB<T, B> implements WeightCollection<T> {
    private final List<WeightPair> list = new ArrayList<>();

    @Override
    public void add(T t, double weight) {
        this.add(t, weight, null);
    }

    public void add(T t, double weight, B bind) {
        if (weight <= 0 || t == null) {
            throw new IllegalArgumentException();
        }
        list.add(new WeightPair(weight, t, bind));
    }

    public List<T> toObjects() {
        return list.stream().map(WeightPair::getT).collect(Collectors.toList());
    }

    public List<B> toBinds() {
        return list.stream().map(WeightPair::getBind).distinct().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Map<B, T> toBindObjectMap() {
        Map<B, T> map = new HashMap<>();
        for (WeightPair tWeightPair : this.list) {
            if (tWeightPair.bind != null) {
                map.put(tWeightPair.bind, tWeightPair.t);
            }
        }
        return map;
    }

    public T get() {
        if (this.list.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        return this.list.get(this.genIndex(this.list)).t;
    }

    public List<T> get(int size) {
        if (size >= this.list.size()) {
            return this.toObjects();
        }
        List<WeightPair> clone = new ArrayList<>(this.list);
        List<T> list = new ArrayList<>();
        while (size > 0) {
            size--;
            int index = this.genIndex(clone);
            list.add(clone.get(index).t);
            clone.remove(index);
        }
        return list;
    }

    private int genIndex(List<WeightPair> weightPairs) {
        double target = ThreadLocalRandom.current()
                .nextDouble(weightPairs.stream().mapToDouble(WeightPair::getWeight).sum());
        int index = 0;
        do {
            target -= weightPairs.get(index).weight;
            index++;
        } while (target > 0 && index < weightPairs.size());
        return index - 1;
    }

    public double getTotalWeight() {
        return this.list.stream().mapToDouble(WeightPair::getWeight).sum();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.stream().anyMatch(tWeightPair -> tWeightPair.t.equals(o));
    }

    @Override
    public Iterator<T> iterator() {
        return this.toObjects().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.toObjects().toArray();
    }

    @Override
    public <Z> Z[] toArray(Z[] a) {
        return this.toObjects().toArray(a);
    }

    @Override
    public boolean add(T t) {
        // add with neutral weight
        this.add(t, isEmpty() ? 50 : getTotalWeight() / this.list.size());
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return this.list.removeIf(tWeightPair -> tWeightPair.t.equals(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(this.list.stream().map(WeightPair::getT).collect(Collectors.toList())).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.list.removeIf(tWeightPair -> c.contains(tWeightPair.t));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.list.removeIf(tWeightPair -> !c.contains(tWeightPair.t));
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @AllArgsConstructor
    @Getter
    private class WeightPair {
        private final double weight;
        private final T t;
        private final B bind;
    }
}
