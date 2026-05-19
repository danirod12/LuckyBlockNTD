package com.github.danirod12.luckyblock.engine.generator;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class SynergyItem {
    private final Material material;
    private final float synweight;
    // 0.0 to 100.0 Indicates how likely item is to be associated with corresponding coreItem

    public SynergyItem(Material material, float synweight) {
        this.material = material;
        this.synweight = synweight;
    }
}
