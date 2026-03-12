package com.github.danirod12.luckyblock.api.customitem;

import org.bukkit.event.block.BlockBreakEvent;

@FunctionalInterface
public interface BreakEvent {
    void execute(BlockBreakEvent e);
}
