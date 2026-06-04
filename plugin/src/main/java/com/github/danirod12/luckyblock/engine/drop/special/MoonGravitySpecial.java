package com.github.danirod12.luckyblock.engine.drop.special;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MoonGravitySpecial implements SpecialLuckyDrop {
    @Override
    public void execute(Execution execution) {
        Player player = execution.getPlayer();
        if (player == null) {
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 7));
    }
}
