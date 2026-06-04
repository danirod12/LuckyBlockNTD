package com.github.danirod12.luckyblock.engine.drop.special;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleportSpecial implements SpecialLuckyDrop {
    @Override
    public void execute(Execution execution) {
        Player player = execution.getPlayer();
        if (player == null) {
            return;
        }

        Location loc = player.getLocation();
        double offsetX = (ThreadLocalRandom.current().nextDouble() * 20) - 10;
        double offsetZ = (ThreadLocalRandom.current().nextDouble() * 20) - 10;

        double newX = loc.getX() + offsetX;
        double newZ = loc.getZ() + offsetZ;

        int newY = loc.getWorld().getHighestBlockYAt((int) newX, (int) newZ);

        Location target = new Location(loc.getWorld(), newX, newY + 1, newZ, loc.getYaw(), loc.getPitch());
        player.teleport(target);
        XSound.ENTITY_ENDERMAN_TELEPORT.play(target, 1.0f, 1.0f);
    }
}
