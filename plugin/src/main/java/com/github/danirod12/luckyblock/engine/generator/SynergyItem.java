package com.github.danirod12.luckyblock.engine.generator;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;

@Getter
public class SynergyItem {
    private final XMaterial material;
    private final float synweight;
    // 0.0 to 100.0 Indicates how likely item is to be associated with corresponding coreItem

    public SynergyItem(XMaterial material, float synweight) {
        this.material = material;
        this.synweight = synweight;
    }
}
