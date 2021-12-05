package me.DenBeKKer.ntdLuckyBlock.economy;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.DenBeKKer.ntdLuckyBlock.LBMain.Hooks;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class VaultEconomy implements EconomyBridge {
	
	public final Economy eco;
	
	public VaultEconomy(Economy eco) {
		this.eco = eco;
	}
	
	public VaultEconomy() {
		
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null && economyProvider.getProvider() != null) {
            this.eco = economyProvider.getProvider();
        } else {
        	Hooks.Vault.disable("economy not found");
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
		String s = "";
		if(eco != null) s = " " + eco.currencyNameSingular();
		if(s.equalsIgnoreCase("  ") || s.equalsIgnoreCase(" ")) s = "";
		return new DecimalFormat("#.##").format(amount) + s;
	}
	
	@Override
	public int getBalance(Player target) {
		return (int) eco.getBalance(target);
	}
	
}
