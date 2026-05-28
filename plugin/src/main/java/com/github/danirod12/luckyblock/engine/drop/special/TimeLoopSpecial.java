package com.github.danirod12.luckyblock.engine.drop.special;

import com.cryptomorin.xseries.XSound;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeLoopSpecial implements SpecialLuckyDrop {
    @Override
    public void execute(Execution execution) {
        final Player player = execution.getPlayer();
        if (player == null) {
            return;
        }

        final Location savePoint = player.getLocation();
        final double savedHealth = player.getHealth();

        player.sendMessage("[!] Time anomaly detected...");

        new BukkitRunnable() {
            int loops = 0;
            @Override
            public void run() {
                if (loops >= 3 || !player.isOnline()) {
                    player.sendMessage("[!] Timeline restored.");
                    this.cancel();
                    return;
                }

                player.teleport(savePoint);
                player.setHealth(savedHealth);
                XSound.ENTITY_ENDERMAN_TELEPORT.play(savePoint, 1.0f, 0.1f);

                loops++;
            }
        }.runTaskTimer(LuckyBlockAPI.getInstance(), 100L, 100L);
    }
}
