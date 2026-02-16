package me.DenBeKKer.ntdLuckyBlock.api.setup;

import me.DenBeKKer.ntdLuckyBlock.api.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;

import java.util.List;
import java.util.function.Predicate;

public interface ItemsBag {

    void add(LuckyEntry... entries);

    default void add(DropChance chance, LuckyDrop... drops) {
        this.add(new LuckyEntry(chance, drops));
    }

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
