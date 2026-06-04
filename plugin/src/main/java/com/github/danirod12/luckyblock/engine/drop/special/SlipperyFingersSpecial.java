package com.github.danirod12.luckyblock.engine.drop.special;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.concurrent.ThreadLocalRandom;

public class SlipperyFingersSpecial implements SpecialLuckyDrop {

    @Override
    public void execute(Execution execution) {
        Player player = execution.getPlayer();
        if (player == null) {
            return;
        }

        PlayerInventory inv = player.getInventory();
        Location loc = player.getLocation();

        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);

            if (item == null || item.getType().name().contains("AIR")) {
                continue;
            }

            if (ThreadLocalRandom.current().nextBoolean()) {
                loc.getWorld().dropItemNaturally(loc, item.clone());
                inv.setItem(i, null);
            }
        }

        XSound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR.play(loc, 1.0f, 0.5f);

        player.updateInventory();
    }
}
