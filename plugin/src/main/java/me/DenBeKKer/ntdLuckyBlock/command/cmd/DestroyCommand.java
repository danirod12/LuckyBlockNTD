package me.DenBeKKer.ntdLuckyBlock.command.cmd;

import java.util.List;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.command.CommandResponce;
import me.DenBeKKer.ntdLuckyBlock.command.LBPlayerCommand;
import me.DenBeKKer.ntdLuckyBlock.util.manager.MessagesManager.Message;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_13;

public class DestroyCommand implements LBPlayerCommand {
	
	@Override
	public boolean permission() { return true; }
	
	@Override
	public final String[] commands() {
		return new String[] { "destroy", "kill" };
	}
	
	@Override
	public Message helpMessage() {
		return Message.CMD_DESTROY;
	}
	
	@Override
	public CommandResponce execute(Player player, String label, String[] args) {
		
		z:
		if(args.length > 0) {
			
			boolean destroy_all = false;
			for(String arg : args)
				if(arg.equalsIgnoreCase("-all")) destroy_all = true;
			
			int removed = 0;
			if(args[0].equalsIgnoreCase("chunk")) {
				
				removed = destroyEntity(player.getLocation().getChunk().getEntities(), destroy_all);
				
			} else {
				
				int r = 0;
				try {
					r = Integer.parseInt(args[0]);
				} catch(Exception ingnored) {}
				if(r <= 0) break z;
				
				List<Entity> list = player.getNearbyEntities(r, r, r);
				removed = destroyEntity(list.toArray(new Entity[list.size()]), destroy_all);
				
			}
			player.sendMessage(Message.CMD_DESTROYED_LB.getAsString(true).replace("%amount%", String.valueOf(removed)));
			return CommandResponce.SUCCESS;
			
		}
		return CommandResponce.SEND_HELP;
		
	}
	
	@SuppressWarnings("deprecation")
	private int destroyEntity(Entity[] array, boolean destroy_all) {
		
		int removed = 0;
		for(Entity entity : array) {
			
			if(entity.getType() != EntityType.ARMOR_STAND) continue;
			ArmorStand stand = (ArmorStand) entity;
			if(stand.getCustomName() == null || !stand.getCustomName().contains(";")) continue;
			
			final LuckyBlockType type = LuckyBlockType.parse(stand.getCustomName().split(";")[0]);
			if(type == null) continue;
			
			final Block block = stand.getWorld().getBlockAt(stand.getLocation().add(-.5, 1.2, -.5));
			if(type.getMaterial() == block.getType()
					&& (!type.isColoredGlass() || LBMain.getInstance().factory instanceof Mat1_13 || block.getData() == type.asColor().getData())) {
				
				if(!destroy_all) continue;
				block.setType(Material.AIR);
				
			}
			entity.remove();
			removed++;
			
		}
		return removed;
		
	}
	
}
