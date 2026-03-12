package com.github.danirod12.luckyblock.api.model;

import java.util.List;

public interface LuckyEntry extends List<LuckyDrop> {

    void setDropChance(DropChance chance);

    DropChance getDropChance();
}
