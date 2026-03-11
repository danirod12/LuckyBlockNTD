package me.DenBeKKer.ntdLuckyBlock.hook.economy;

import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;

public class VaultEconomy implements EconomyBridge {

    public final Economy eco;

    public VaultEconomy(Economy eco) {
        this.eco = eco;
    }

    public VaultEconomy() {
        RegisteredServiceProvider<Economy> economyProvider
                = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null && economyProvider.getProvider() != null) {
            this.eco = economyProvider.getProvider();
        } else {
            Hook.Vault.disable("economy not found");
            this.eco = null;
        }
    }

    @Override
    public boolean enabled() {
        return eco != null;
    }

    @Override
    public boolean withdraw(Player target, int amount) {
        return eco.withdrawPlayer(target, amount).type == ResponseType.SUCCESS;
    }

    @Override
    public String format(int amount) {
        String currencyName = "";
        if (eco != null) {
            currencyName = " " + eco.currencyNameSingular();
        }
        if (currencyName.equalsIgnoreCase("  ") || currencyName.equalsIgnoreCase(" ")) {
            currencyName = "";
        }
        return new DecimalFormat("#.##").format(amount) + currencyName;
    }

    @Override
    public int getBalance(Player target) {
        return (int) eco.getBalance(target);
    }
}
