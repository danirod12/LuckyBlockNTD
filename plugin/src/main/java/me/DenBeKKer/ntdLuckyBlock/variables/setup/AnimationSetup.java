package me.DenBeKKer.ntdLuckyBlock.variables.setup;

import org.bukkit.Effect;

public class AnimationSetup {

    private final boolean enabled;
    private final Effect effect;

    public AnimationSetup(boolean enabled, Effect effect) {
        this.enabled = enabled;
        this.effect = effect;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Effect getEffect() {
        return effect;
    }
}
