package me.DenBeKKer.ntdLuckyBlock.variables;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Deprecated
public class LuckyItem {
	
	@Deprecated
	public enum LuckyItemType {
		LUCKY_BLOCK_ITEM, ITEM, SPECIAL, ENTITY, COMMAND, CONSOLE, MESSAGE;
	}
	
	@Deprecated
	public enum Special {
		PIG, LIGHTNING, WATER_BUCKET, DIAMOND_COLUMN;
		
		@Deprecated
		public int defaultValue() { throw new UnsupportedOperationException("Special moved to me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop since 2.1.0"); }
		
		@Deprecated
		public void execute(Object... obj) { throw new UnsupportedOperationException("Special moved to me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop since 2.1.0"); }
		
	}
	
//		
//		public void execute(Block b, Player target, int arg) {
//			
//			switch(this) {
//			case LIGHTNING:
//				
//				new BukkitRunnable() {
//					
//					int i = 0;
//					
//					@Override
//					public void run() {
//						
//						if(i >= arg || !target.isOnline()) {
//							cancel();
//							return;
//						}
//						b.getWorld().strikeLightning(target.getLocation());
//						i++;
//						
//					}
//					
//				}.runTaskTimer(LBMain.getInstance(), 15, 15);
//				
//				break;
//			case PIG:
//				
//				Pig current = null;
//				for(int i = 0; i < arg; i++) {
//					Pig pig = (Pig) b.getWorld().spawnEntity(b.getLocation().add(0.5, 0.4, 0.5), EntityType.PIG);
//					pig.setCanPickupItems(false);
//					pig.setRemoveWhenFarAway(true);
//					if(current != null) 
//						current.setPassenger(pig);
//					current = pig;
//				}
//				current.setPassenger(target);
//				
//				break;
//			case WATER_BUCKET:
//				
//				//target.sendMessage(Main.getInstance().config.get().getString("messages.water_bucket").replace("&", "\u00a7"));
//				target.sendMessage(Message.WATER_BUCKET.get());
//				
//				target.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
//				target.teleport(target.getLocation().add(0, arg, 0));
//				
//				break;
//			case DIAMOND_COLUMN:
//				
//				summonColumn(target.getLocation().getBlock());
//				break;
//				
//			default:
//				break;
//			}
//			
//		}
//		
//		public void execute(Block b, int arg) {
//			
//			switch(this) {
//			case LIGHTNING:
//				b.getWorld().strikeLightning(b.getLocation().add(0.5, 0.5, 0.5));
//				break;
//			case DIAMOND_COLUMN:
//				
//				summonColumn(b);
//				break;
//				
//			case PIG:
//				
//				Pig current = null;
//				for(int i = 0; i < arg; i++) {
//					Pig pig = (Pig) b.getWorld().spawnEntity(b.getLocation().add(0.5, 0.4, 0.5), EntityType.PIG);
//					pig.setCanPickupItems(false);
//					pig.setRemoveWhenFarAway(true);
//					if(current != null) 
//						current.setPassenger(pig);
//					current = pig;
//				}
//				
//				break;
//			case WATER_BUCKET:
//				
//				b.getWorld().dropItem(b.getLocation().add(0.5, 1, 0.5), new ItemStack(Material.WATER_BUCKET));
//				
//				break;
//			default:
//				break;
//			}
//			
//		}
//		
//		public int defaultValue() {
//			
//			switch(this) {
//			case LIGHTNING: return 3;
//			case PIG: return 4;
//			case WATER_BUCKET: return 65;
//			default: return 1;
//			}
//			
//		}
//		
//	}
//	
////	private ItemStack item = null;
////	private Special special;
////	private LuckyBlockType luckyblock;
//	private LuckyItemType type;
//	private Object object;
//	private int arg;
	
	@Deprecated
	public LuckyItem(LuckyItemType type, Object obj, int arg) {
		throw new UnsupportedOperationException("LuckyItem no longer supported since 2.1.0! Use LuckyDrop");
//		this.type = type;
//		
//		switch(type) {
//		case ENTITY:
//			if(!(obj instanceof EntityType)) {
//				throw new IllegalArgumentException("You must provide EntityType fot " + type.name());
//			}
//			break;
//		case ITEM:
//			if(!(obj instanceof ItemStack)) {
//				throw new IllegalArgumentException("You must provide ItemStack fot " + type.name());
//			}
//			break;
//		case LUCKY_BLOCK_ITEM:
//			if(!(obj instanceof LuckyBlockType)) {
//				throw new IllegalArgumentException("You must provide LuckyBlockType fot " + type.name());
//			}
//			break;
//		case SPECIAL:
//			if(!(obj instanceof Special)) {
//				throw new IllegalArgumentException("You must provide Special fot " + type.name());
//			}
//			break;
//		case COMMAND: case MESSAGE: case CONSOLE:
//			if(!(obj instanceof String)) {
//				throw new IllegalArgumentException("You must provide String fot " + type.name());
//			}
//		default:
//			break;
//		}
//		
//		this.object = obj;
//		this.arg = arg;
		
	}
	
//	public static void summonColumn(Block block) {
//		
//		if(collection == null) return;
//		int y = collection.size() + 10;
//		
//		new BukkitRunnable() {
//			
//			List<Material> col1 = new ArrayList<>(collection);
//			short u = 4;
//			@Override
//			public void run() {
//				
//				if(u == 4 && col1.size() == 0) {
//					block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, y, 0.5), Material.DIAMOND_BLOCK, (byte)0);
//					u = 3;
//					return;
//				}
//				
//				if(u <= 3) {
//					u--;
//					if(u <= 0) {
//						if(block.getWorld().getBlockAt(block.getLocation().add(0.5, y - 9, 0.5)).getType() == Material.AIR) {
//							block.getWorld().getBlockAt(block.getLocation().add(0.5, y - 9, 0.5)).setType(Material.FIRE);
//						}
//						block.getWorld().strikeLightning(block.getLocation().add(0.5, y - 10, 0.5));
//						cancel();
//					}
//					return;
//				}
//				
//				block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, y, 0.5), col1.get(0), (byte)0);
//				col1.remove(0);
//				
//			}
//			
//		}.runTaskTimer(LBMain.getInstance(), 10, 10);
//		
//	}
	
	@Deprecated
	public void execute(Block b, Player target) {
		throw new UnsupportedOperationException("LuckyItem no longer supported since 2.1.0! Use LuckyDrop"); }
		
//		switch(type) {
//		case ITEM:
//			if(object != null) {
//				b.getWorld().dropItem(b.getLocation().add(0.5, 0.4, 0.5), ((ItemStack)object).clone());
//			}
//			break;
//		case SPECIAL:
//			
//			if(object != null) {
//				if(target != null) {
//					((Special)object).execute(b, target, arg);
//				} else ((Special)object).execute(b, arg);
//			}
//			break;
//		case LUCKY_BLOCK_ITEM:
//			
//			if(object != null && ((LuckyBlockType)object) != null) {
//				ItemStack item = ((LuckyBlockType)object).get().getSkull();
//				item.setAmount(arg);
//				b.getWorld().dropItem(b.getLocation().add(0.5, 0.4, 0.5), item);
//				item.setAmount(1);
//			}
//			break;
//		case ENTITY:
//			
//			if(object != null) {
//				for(int i = 0; i < arg; i++)
//					b.getWorld().spawnEntity(b.getLocation().add(0.5, 1, 0.5), (EntityType) object);
//			}
//			
//			break;
//		case CONSOLE:
//			if(((String)object).contains("%player%") && target == null) break;
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ((String)object).replace("%player%", target.getName()));
//			break;
//		case COMMAND:
//			if(target != null)
//				Bukkit.dispatchCommand(target, ((String)object).replace("%player%", target.getName()));
//			break;
//		case MESSAGE:
//			if(target != null)
//				target.sendMessage(((String)object).replace("%player%", target.getName()).replace("&", "\u00a7"));
//		default:
//			break;
//		}
//		
//	}

	@Deprecated
	public void execute(Block b) { execute(b, null); }
	
//	static Collection<Material> collection;
	@Deprecated
	public void special(Collection<Material> collection12) {
		throw new UnsupportedOperationException("LuckyItem no longer supported since 2.1.0! Use LuckyDrop");
//		collection = collection12;
	}
	
}
