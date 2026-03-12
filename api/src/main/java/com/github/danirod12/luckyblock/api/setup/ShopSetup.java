package com.github.danirod12.luckyblock.api.setup;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShopSetup {
    private final boolean enabled;
    private final double price;
}
