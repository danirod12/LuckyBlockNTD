package me.DenBeKKer.ntdLuckyBlock.engine.model;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.model.DropChance;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;

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
