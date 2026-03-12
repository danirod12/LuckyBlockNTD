package com.github.danirod12.luckyblock.api.setup;

import com.github.danirod12.luckyblock.api.model.DropChance;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.LuckyEntry;

import java.util.List;
import java.util.function.Predicate;

public interface ItemsBag {

    void add(LuckyEntry... entries);

    void add(DropChance chance, LuckyDrop... drops);

    void removeEntryIf(Predicate<LuckyEntry> predicate);

    void removeDropIf(Predicate<LuckyDrop> predicate);

    default void remove(LuckyDrop drop) {
        removeDropIf(value -> value == drop);
    }

    void remove(LuckyEntry entry);

    List<LuckyEntry> getEntries();

    int size();

    LuckyEntry getEntry();

    void reset();

    int getTotalDropsAmount();
}
