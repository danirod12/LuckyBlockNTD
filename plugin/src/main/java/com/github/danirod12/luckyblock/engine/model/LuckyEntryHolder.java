package com.github.danirod12.luckyblock.engine.model;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.model.DropChance;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.LuckyEntry;

import java.util.ArrayList;

public class LuckyEntryHolder extends ArrayList<LuckyDrop> implements LuckyEntry {

    private DropChance chance = DropChance.MEDIUM;

    public LuckyEntryHolder() {
    }

    public LuckyEntryHolder(DropChance dropChance) {
        setDropChance(dropChance);
    }

    @Override
    public void setDropChance(DropChance chance) {
        this.chance = chance;
    }

    @Override
    public DropChance getDropChance() {
        return LuckyBlockAPI.getVersionType().isFree() ? DropChance.MEDIUM : chance;
    }
}
