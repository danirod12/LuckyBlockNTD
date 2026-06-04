package com.github.danirod12.luckyblock.engine.generator;

import lombok.Getter;
import com.cryptomorin.xseries.XMaterial;

import java.util.List;

@Getter
public class CoreItem {
    private final XMaterial material;
    private final int tier;
    private final List<SynergyItem> synergies;
    private final SynergyMode mode;

    public CoreItem(XMaterial material, int tier, List<SynergyItem> synergies, SynergyMode mode) {
        this.material = material;
        this.tier = tier;
        this.synergies = synergies;
        this.mode = mode;
    }
}
