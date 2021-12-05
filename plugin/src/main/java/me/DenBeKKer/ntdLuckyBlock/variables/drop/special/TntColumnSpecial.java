package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.annotations.SerializedName;

import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public class TntColumnSpecial implements LuckyDrop {
	
	@SerializedName(value = "amount")
	private int a;
	
	public TntColumnSpecial(int a) {
		this.a = a;
	}
	
	@Override
	public void execute(Block b, Player target) {
		execute(b);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(Block b) {
		
		new BukkitRunnable() {
			
			int i = a;
			
			@Override
			public void run() {
				
				if(i == a)
					b.setType(Material.REDSTONE_BLOCK);
				
				b.getWorld().spawnFallingBlock(b.getLocation().add(.5, 4, .5), Material.TNT, (byte)0);
				i--;
				if(i <= 0)
					cancel();
				
			}
			
		}.runTaskTimer(LBMain.getInstance(), 10, 10);
		
	}
	
}
