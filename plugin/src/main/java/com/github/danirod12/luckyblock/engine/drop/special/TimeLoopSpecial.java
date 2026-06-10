package com.github.danirod12.luckyblock.engine.drop.special;

import com.cryptomorin.xseries.XSound;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.folia.ManagedRunnable;
import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

        SchedulerManager.runTimerAt(LuckyBlockAPI.getInstance(), savePoint, new ManagedRunnable() {
            int loops = 0;
            @Override
            public void run() {
                if (loops >= 3 || !player.isOnline()) {
                    player.sendMessage("§a[!] Timeline restored.");
                    this.cancel();
                    return;
                }

                player.teleport(savePoint);
                player.setHealth(savedHealth);
                XSound.ENTITY_ENDERMAN_TELEPORT.play(savePoint, 1.0f, 0.1f);

                loops++;
            }
        }, 100L, 100L);
    }
}
