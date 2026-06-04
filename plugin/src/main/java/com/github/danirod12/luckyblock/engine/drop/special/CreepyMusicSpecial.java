package com.github.danirod12.luckyblock.engine.drop.special;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CreepyMusicSpecial implements SpecialLuckyDrop {

    @Override
    public void execute(Execution execution) {
        Player player = execution.getPlayer();
        if (player == null) {
            return;
        }

        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();

        if (XMaterial.JUKEBOX.parseMaterial() != null) {
            blockBelow.setType(XMaterial.JUKEBOX.parseMaterial());
        }

        Material disc11 = XMaterial.MUSIC_DISC_11.parseMaterial();

        if (XSound.MUSIC_DISC_11.isSupported()) {
            XSound.MUSIC_DISC_11.play(player.getLocation(), 1.0f, 1.0f);
        } else {
            if (disc11 != null) {
                player.playEffect(blockBelow.getLocation(), Effect.RECORD_PLAY, disc11);
            }
        }
    }
}
