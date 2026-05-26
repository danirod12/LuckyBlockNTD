package com.github.danirod12.luckyblock.engine.drop.special;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ParanoiaSpecial implements SpecialLuckyDrop {

    @Override
    public void execute(Execution execution) {
        Player player = execution.getPlayer();
        if (player == null) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));

        Location behindPlayer = player.getLocation().clone()
                .subtract(player.getLocation().getDirection().normalize().multiply(1.5));

        XSound.ENTITY_CREEPER_PRIMED.play(behindPlayer, 1.0f, 1.0f);
        XSound.ENTITY_ITEM_BREAK.play(behindPlayer, 1.0f, 1.0f);
    }
}
