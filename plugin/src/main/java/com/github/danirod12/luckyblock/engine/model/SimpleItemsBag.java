package com.github.danirod12.luckyblock.engine.model;

import com.github.danirod12.luckyblock.api.model.DropChance;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.LuckyEntry;
import com.github.danirod12.luckyblock.api.setup.ItemsBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class SimpleItemsBag implements ItemsBag {

    private final List<LuckyEntry> items = new ArrayList<>();

    @Override
    public void add(LuckyEntry... entries) {
        items.addAll(Arrays.asList(entries));
    }

    @Override
    public void add(DropChance chance, LuckyDrop... drops) {
        LuckyEntryHolder holder = new LuckyEntryHolder(chance);
        Collections.addAll(holder, drops);
        this.add(holder);
    }

    @Override
    public void removeEntryIf(Predicate<LuckyEntry> predicate) {
        this.items.removeIf(predicate);
    }

    @Override
    public void removeDropIf(Predicate<LuckyDrop> predicate) {
        for (LuckyEntry entry : new ArrayList<>(this.items)) {
            entry.removeIf(predicate);
            if (entry.isEmpty()) {
                this.items.remove(entry);
            }
        }
    }

    @Override
    public void remove(LuckyEntry entry) {
        this.items.remove(entry);
    }

    @Override
    public List<LuckyEntry> getEntries() {
        return new ArrayList<>(this.items);
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public LuckyEntry getEntry() {
        return this.items.get(ThreadLocalRandom.current().nextInt(this.items.size()));
    }

    @Override
    public void reset() {
        this.items.clear();
    }

    @Override
    public int getTotalDropsAmount() {
        return getEntries().stream().mapToInt(LuckyEntry::size).sum();
    }
}
