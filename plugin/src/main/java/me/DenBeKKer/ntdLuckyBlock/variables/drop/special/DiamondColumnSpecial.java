package me.DenBeKKer.ntdLuckyBlock.variables.drop.special;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiamondColumnSpecial implements LuckyDrop {
	
	@SerializedName(value = "materials")
	private final Collection<Material> collection;

	public Collection<Material> getMaterials() {
		return collection;
	}

	public DiamondColumnSpecial(Collection<Material> collection) {
		this.collection = collection;
	}

	@Override
	public void execute(LBMain.LuckyBlockType type, Block b, Player target) {
		this.summonColumn(target == null ? b : target.getLocation().getBlock());
	}

	public void summonColumn(Block block) {
		
		if(collection == null) return;
		int y = collection.size() + 10;
		
		new BukkitRunnable() {
			
			List<Material> col1 = new ArrayList<>(collection);
			short u = 4;
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				if(u == 4 && col1.size() == 0) {
					block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, y, 0.5), Material.DIAMOND_BLOCK, (byte)0);
					u = 3;
					return;
				}
				
				if(u <= 3) {
					u--;
					if(u <= 0) {
						if(block.getWorld().getBlockAt(block.getLocation().add(0.5, y - 9, 0.5)).getType() == Material.AIR) {
							block.getWorld().getBlockAt(block.getLocation().add(0.5, y - 9, 0.5)).setType(Material.FIRE);
						}
						block.getWorld().strikeLightning(block.getLocation().add(0.5, y - 10, 0.5));
						cancel();
					}
					return;
				}
				
				block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, y, 0.5), col1.get(0), (byte)0);
				col1.remove(0);
				
			}
			
		}.runTaskTimer(LBMain.getInstance(), 10, 10);
		
	}
	
}
