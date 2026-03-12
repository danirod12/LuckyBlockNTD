package com.github.danirod12.luckyblock.hook.economy;

import com.github.danirod12.luckyblock.util.Misc;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TokenManagerEconomy implements EconomyBridge {

    private final String display;
    private final TokenManager instance;

    public TokenManagerEconomy(String display) {
        this.display = Misc.setColors(display);
        this.instance = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
    }

    @Override
    public int getBalance(Player target) {
        return (int) instance.getTokens(target).orElse(0);
    }

    @Override
    public boolean withdraw(Player target, int amount) {
        return instance.removeTokens(target, amount);
    }

    @Override
    public String format(int amount) {
        return display.replace("%amount%", String.valueOf(amount));
    }

    @Override
    public boolean enabled() {
        return true;
    }
}
