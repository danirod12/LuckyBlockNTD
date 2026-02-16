package me.DenBeKKer.ntdLuckyBlock.api.model;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface LuckyDrop {

    void execute(LuckyBlockKey related, Block block, Player player);
}
