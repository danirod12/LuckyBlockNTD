package com.github.danirod12.luckyblock.api.model;

import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents a drop that can be executed when a lucky block is broken.
 */
public interface LuckyDrop {

    /**
     * Executes the drop.
     *
     * @param execution the execution context containing necessary information for the drop
     */
    void execute(Execution execution);

    /**
     * Represents the context in which a lucky drop is executed.
     */
    @Data
    class Execution {
        private final Plugin instance;
        private final LuckyBlockKey key;
        private final Block block;
        private final Player player;
    }
}
