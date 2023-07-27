package me.DenBeKKer.ntdLuckyBlock.hook.sk89q;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import me.DenBeKKer.ntdLuckyBlock.util.IWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class WorldEdit6 implements IWorldEdit {

    private final WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

    @Override
    public void paste(File file, Block obj, boolean a, boolean air, List<String> blacklist) {

        try {
            CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
            EditSession session = worldedit.getWorldEdit().getEditSessionFactory()
                    .getEditSession(new BukkitWorld(obj.getWorld()), -1);
            if (blacklist.size() > 0) {
                BlockMask blockMask = new BlockMask(session);
                for (String block : blacklist) {
                    try {
                        BlockType type = BlockType.valueOf(block.toUpperCase());
                        blockMask.add(new BaseBlock(type.getID()));
                    } catch (Exception ignored) {
                    }
                }
                session.setMask(Masks.negate(blockMask));
            }
            clipboard.paste(session, new Vector(obj.getX(), obj.getY(), obj.getZ()), !air);
        } catch (Exception e) {
            e.printStackTrace();
            MvLogger.log(Level.SEVERE, "Something went wrong");
        }

    }

//	private final WorldEditPlugin worldedit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
//
//	@Override
//	public void paste(File file, final Block obj, boolean a) {
//
//		try {
//			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
//			EditSession session = worldedit.getWorldEdit().getEditSessionFactory()
//					.getEditSession(new BukkitWorld(obj.getWorld()), -1);
//
//			final Vector origin;
//			clipboard.paste(session, origin = new Vector(obj.getX(), obj.getY(), obj.getZ()), true);
//
////			min = max.add(clipboard.getWidth(), -clipboard.getHeight(), clipboard.getLength());
//			Vector min = origin.add(clipboard.getOffset());
//			Vector max = min.add(clipboard.getSize());
//
//			Bukkit.getScheduler().runTaskLater(MvLogger.getInstance(), () -> {
//
//				try {
//
//					int chunkZ = min.getBlockZ() >> 4 - 1;
//					for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
//						for(int z = min.getBlockX(); z <= max.getBlockX(); z++) {
//
//							Bukkit.broadcastMessage("x: " + x + ", z: " + z);
//
//							int i0 = z >> 4;
//							if(i0 != chunkZ) {
//
//								Chunk chunk = obj.getWorld().getChunkAt(x >> 4, i0);
//
//								List<Entity> list = new ArrayList<>();
//								for(Entity entity : chunk.getEntities()) {
//									Bukkit.broadcastMessage(entity.getType().name());
//									if(entity.getType() != EntityType.ARMOR_STAND)
//										continue;
//									Location location = entity.getLocation();
//									if(location.getX() >= min.getX() && location.getZ() >= min.getZ() && location.getY() >= min.getY() - 1.2D &&
//											location.getX() <= max.getX() && location.getZ() <= max.getZ() && location.getY() <= max.getY()) {
//										list.add(entity);
//									}
//								}
//
//								if(list.size() > 0)
//									destroyEntity.invoke(null, list.toArray(new Entity[0]), false);
//
//								chunkZ = i0;
//
//							}
//
//							for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
//								Block block;
//								resolveSign.invoke(null, block = obj.getWorld().getBlockAt(x, y, z), a);
//								block.setType(Material.STAINED_GLASS);
//							}
//
//						}
//					}
//
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					MvLogger.log(Level.SEVERE, "Something went wrong");
//				}
//
//			}, 1L);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			MvLogger.log(Level.SEVERE, "Something went wrong");
//		}
//
//	}
//
//	private static final Method resolveSign, destroyEntity;
//
//	static {
//
//		Method method0, method1;
//		try {
//
//			Class<?> clazz = Class.forName("me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI");
//			method0 = clazz.getDeclaredMethod("resolveSign", Block.class, boolean.class);
//			method1 = clazz.getDeclaredMethod("destroyEntity", Entity[].class, boolean.class);
//
//		} catch (Throwable e) {
//			e.printStackTrace();
//			method0 = method1 = null;
//		}
//
//		resolveSign = method0;
//		destroyEntity = method1;
//
//	}

}
