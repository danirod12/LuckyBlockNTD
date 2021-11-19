package me.DenBeKKer.ntdLuckyBlock.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.realized.tokenmanager.api.TokenManager;

public class TokenManagerEconomy implements EconomyBridge {
	
	private String display;
	private TokenManager instance;
	
	public TokenManagerEconomy(String display) {
		this.display = Misc.setColors(display);
		this.instance = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");;
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
