package com.github.danirod12.luckyblock.engine.drop.special;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.folia.ManagedRunnable;
import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class BlackHoleSpecial implements SpecialLuckyDrop {
    @Override
    public void execute(Execution execution) {
        final Location center = execution.getBlock().getLocation().add(0.5, 0.5, 0.5);

        SchedulerManager.runTimerAt(LuckyBlockAPI.getInstance(), center, new ManagedRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 200) {
                    this.cancel();
                    return;
                }

                center.getWorld().playEffect(center, Effect.SMOKE, 4);

                for (Entity entity : center.getWorld().getNearbyEntities(center, 10, 10, 10)) {
                    Vector trajectory = center.toVector().subtract(entity.getLocation().toVector());

                    if (trajectory.lengthSquared() > 0.5) {
                        trajectory.normalize().multiply(0.2);
                        entity.setVelocity(entity.getVelocity().add(trajectory));
                    }
                }
                ticks++;
            }
        }, 0L, 1L);
    }
}
