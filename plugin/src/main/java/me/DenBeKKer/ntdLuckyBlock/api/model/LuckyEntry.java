package me.DenBeKKer.ntdLuckyBlock.api.model;

import me.DenBeKKer.ntdLuckyBlock.api.DropChance;

import java.util.List;

public interface LuckyEntry extends List<LuckyDrop> {

    void setDropChance(DropChance chance);

    DropChance getDropChance();
}
