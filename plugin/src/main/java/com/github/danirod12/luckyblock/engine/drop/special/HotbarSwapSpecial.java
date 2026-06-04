package com.github.danirod12.luckyblock.engine.drop.special;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotbarSwapSpecial implements SpecialLuckyDrop {

    @Override
    public void execute(Execution execution) {
        Player player = execution.getPlayer();
        if (player == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        List<ItemStack> hotbar = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            hotbar.add(inventory.getItem(i));
        }

        Collections.shuffle(hotbar);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, hotbar.get(i));
        }

        player.updateInventory();
    }
}
