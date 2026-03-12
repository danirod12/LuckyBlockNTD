package com.github.danirod12.luckyblock.hook.sk89q;

import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.model.IWorldEdit;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WorldEdit7 implements IWorldEdit {

    private static final Method RESOLVE_SIGN, DESTROY_ENTITY;

    static {
        Method method0, method1;
        try {
            Class<?> clazz = Class.forName("com.github.danirod12.luckyblock.api.LuckyBlockAPI");
            method0 = clazz.getDeclaredMethod("resolveSign", Block.class, boolean.class);
            method1 = clazz.getDeclaredMethod("destroyEntity", Entity[].class, boolean.class);
        } catch (Throwable e) {
            e.printStackTrace();
            method0 = method1 = null;
        }

        RESOLVE_SIGN = method0;
        DESTROY_ENTITY = method1;
    }

    public void paste(File file, Block obj, boolean a, boolean ignoreAir, List<String> blacklist) {

        Clipboard clipboard = null;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (Exception e) {
            e.printStackTrace();
            LuckyBlockAPI.getLogger().log(Level.SEVERE,
                    "Schematic " + file.getPath() + " not found or something went wrong");
        }

        if (clipboard == null) {
            LuckyBlockAPI.getLogger().log(Level.SEVERE,
                    "Schematic " + file.getPath() + " not found or something went wrong");
            return;
        }

        try (EditSession editSession = createEditSession(obj.getWorld())) {
            if (blacklist.size() > 0) {
                BlockTypeMask blockMask = new BlockTypeMask(editSession);
                for (String block : blacklist) {
                    BlockType type = BlockTypes.get(block.toLowerCase());
                    if (type != null) {
                        blockMask.add(type);
                    }
                }
                editSession.setMask(Masks.negate(blockMask));
            }
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            Operation operation = holder
                    .createPaste(editSession)
                    .ignoreAirBlocks(ignoreAir)
                    .to(BukkitAdapter.asBlockVector(obj.getLocation()))
                    .build();
            Operations.complete(operation);

            if (clipboard instanceof BlockArrayClipboard) {
                Region region = clipboard.getRegion();
                BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
                Vector3 realTo = BukkitAdapter.asVector(obj.getLocation()).add(holder
                        .getTransform().apply(clipboardOffset.toVector3()));
                Vector3 max = realTo.add(holder.getTransform().apply(region.getMaximumPoint()
                        .subtract(region.getMinimumPoint()).toVector3()));

                Bukkit.getScheduler().runTaskLater(LuckyBlockAPI.getInstance(),
                        () -> formatPastedSchematic(obj.getWorld(),
                                new CuboidRegion(realTo.toBlockPoint(), max.toBlockPoint()), a), 1);
            }
        } catch (WorldEditException e) {
            e.printStackTrace();
            LuckyBlockAPI.getLogger().log(Level.SEVERE, "Something went wrong");
        }
    }

    private void formatPastedSchematic(World world, CuboidRegion region, boolean a) {
        try {
            List<Entity> list = new ArrayList<>();

            BlockVector3 min = region.getMinimumPoint(), max = region.getMaximumPoint();
            for (BlockVector2 vector : region.getChunks()) {

                Chunk chunk = world.getChunkAt(vector.getBlockX(), vector.getBlockZ());
                for (Entity entity : chunk.getEntities()) {
                    Location location = entity.getLocation();
                    if (location.getX() >= min.getX() && location.getZ() >= min.getZ()
                            && location.getY() >= min.getY() - 1.2D && location.getX() <= max.getX()
                            && location.getZ() <= max.getZ() && location.getY() <= max.getY()) {
                        list.add(entity);
                    }
                }
            }
            DESTROY_ENTITY.invoke(null, list.toArray(new Entity[0]), true);

            for (BlockVector3 vector : region) {
                Block block = world.getBlockAt(vector.getX(), vector.getY(), vector.getZ());
                RESOLVE_SIGN.invoke(null, block, a);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private EditSession createEditSession(World world) {
        try {
            return WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
        } catch (Throwable th) {
            return WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1);
        }
    }
}
