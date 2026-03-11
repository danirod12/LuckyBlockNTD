package me.DenBeKKer.ntdLuckyBlock.customitem;

import org.bukkit.event.block.BlockBreakEvent;

@FunctionalInterface
public interface BreakEvent {
    void execute(BlockBreakEvent e);
}
