package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class LightningSpecial implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private int a;
	
	public LightningSpecial(int a) {
		this.a = a;
	}
	
	@Override
	public void execute(Block b, Player target) {
		
		new BukkitRunnable() {
			
			int i = 0;
			
			@Override
			public void run() {
				
				if(i >= a || !target.isOnline() || target.isDead()) {
					cancel();
					return;
				}
				b.getWorld().strikeLightning(target.getLocation());
				i++;
				
			}
			
		}.runTaskTimer(LBMain.getInstance(), 15, 15);
		
	}
	
	@Override
	public void execute(Block b) {
		b.getWorld().strikeLightning(b.getLocation().add(0.5, 0.5, 0.5));
	}
	
}
