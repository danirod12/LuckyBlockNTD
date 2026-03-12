package me.DenBeKKer.ntdLuckyBlock.api.model;

import me.DenBeKKer.ntdLuckyBlock.api.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public enum DropChance {

    LOWEST(1),
    LOW(3),
    MEDIUM(6),
    HIGH(15),
    HIGHEST(25),
    ;

    private int weight;

    DropChance(int weight) {
        this.weight = weight;
    }

    public static DropChance random() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }

    public static DropChance random(List<DropChance> chances) {
        if (chances.isEmpty()) {
            throw new UnsupportedOperationException("Chances mismatch, random() got an empty List");
        }
        if (chances.size() == 1) {
            return chances.get(0);
        }

        List<Pair<Integer, DropChance>> list = new ArrayList<>();
        for (int i = 0; i < chances.size(); i++) {
            int boot = i == 0 ? 0 : list.get(i - 1).getA();
            DropChance chance = chances.get(i);
            list.add(new Pair<>(chance.weight + boot, chance));
        }
        int boot = ThreadLocalRandom.current().nextInt(list.get(list.size() - 1).getA()) + 1;
        for (Pair<Integer, DropChance> pair : list) {
            if (boot <= pair.getA()) {
                return pair.getB();
            }
        }
        throw new RuntimeException(boot + " dropped out of " + list.get(list.size() - 1).getA());
    }

    @Deprecated
    public static int chance(List<DropChance> chances, DropChance chance) {
        return (int) getChanceOf(chances, chance);
    }

    public static double getChanceOf(List<DropChance> chances, DropChance chance) {
        if (!chances.contains(chance)) {
            return 0.0D;
        }
        int boot = chances.stream().mapToInt(DropChance::getWeight).sum();
        return chance.weight * 100D / boot;
    }

    public static double getChanceOf(List<LuckyEntry> drops, LuckyEntry entry) {
        return getChanceOf(drops.stream().map(LuckyEntry::getDropChance)
                .distinct().collect(Collectors.toList()), entry.getDropChance()) / drops.stream()
                .filter(item -> item.getDropChance() == entry.getDropChance()).count();
    }

    public static DropChance parse(String key) {
        for (DropChance value : values()) {
            if (value.name().equalsIgnoreCase(key)) {
                return value;
            }
        }
        return null;
    }

    @Deprecated
    public int getChance() {
        return this.getWeight();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public LuckyEntry roll(List<LuckyEntry> items) {
        List<LuckyEntry> list = items.stream().filter(n -> n.getDropChance() == this).collect(Collectors.toList());
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
