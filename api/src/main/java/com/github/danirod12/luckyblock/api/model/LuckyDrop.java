package com.github.danirod12.luckyblock.api.model;

import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface LuckyDrop {

    void execute(Execution execution);

    @Data
    class Execution {
        private final Plugin instance;
        private final LuckyBlockKey key;
        private final Block block;
        private final Player player;
    }
}
