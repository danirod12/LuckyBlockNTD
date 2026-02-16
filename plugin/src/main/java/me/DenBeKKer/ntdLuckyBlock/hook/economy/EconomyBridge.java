package me.DenBeKKer.ntdLuckyBlock.hook.economy;

import org.bukkit.entity.Player;

public interface EconomyBridge {

    boolean withdraw(Player target, int amount);

    int getBalance(Player target);

    String format(int amount);

    boolean enabled();
}
