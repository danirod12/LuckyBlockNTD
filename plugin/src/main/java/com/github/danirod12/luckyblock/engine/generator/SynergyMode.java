package com.github.danirod12.luckyblock.engine.generator;

public enum SynergyMode {
    STRICT, // Only synergies
    UNLOCKED, // Pure chaos: synergies + all other items TODO(zhabka_zhaba): are their chances equal?
    IGNORE // Ignore synergies, generate V2-style solo item
}
