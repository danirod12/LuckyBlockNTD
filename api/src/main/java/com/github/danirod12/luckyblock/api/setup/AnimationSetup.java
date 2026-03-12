package com.github.danirod12.luckyblock.api.setup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Effect;

@AllArgsConstructor
@Getter
public class AnimationSetup {
    private final boolean enabled;
    private final Effect effect;
}
