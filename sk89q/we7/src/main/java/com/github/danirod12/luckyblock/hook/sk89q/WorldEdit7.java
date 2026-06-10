package com.github.danirod12.luckyblock.hook.sk89q;

import com.github.danirod12.luckyblock.api.folia.SchedulerManager;
import com.github.danirod12.luckyblock.api.model.IWorldEdit;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.provider.LuckyEngineProvider;
import com.github.danirod12.luckyblock.api.util.Pair;
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
import lombok.AllArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@AllArgsConstructor
public class WorldEdit7 implements IWorldEdit {
    private final Plugin plugin;
    private final LuckyEngineProvider provider;

    public void paste(File file, Block obj, boolean ignoreAir, List<String> blacklist) {
        Clipboard clipboard = null;
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (clipboard == null) {
            this.plugin.getLogger().log(Level.SEVERE,
                    "Schematic " + file.getPath() + " not found or something went wrong");
            return;
        }

        try (EditSession editSession = createEditSession(obj.getWorld())) {
            if (!blacklist.isEmpty()) {
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
            Operations.complete(operation); // TODO pass here something about FAWE and activate physics? To be checked

            if (clipboard instanceof BlockArrayClipboard) {
                Region region = clipboard.getRegion();
                BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
                Vector3 realTo = BukkitAdapter.asVector(obj.getLocation()).add(holder
                        .getTransform().apply(clipboardOffset.toVector3()));
                Vector3 max = realTo.add(holder.getTransform().apply(region.getMaximumPoint()
                        .subtract(region.getMinimumPoint()).toVector3()));

                SchedulerManager.runLaterAt(this.plugin, obj.getLocation(), () -> formatPastedSchematic(obj.getWorld(),
                        new CuboidRegion(realTo.toBlockPoint(), max.toBlockPoint())), 1);
            }
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private void formatPastedSchematic(World world, CuboidRegion region) {
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
            for (Pair<LuckyBlockKey, ArmorStand> entity : this.provider.searchByEntities(list)) {
                entity.getValue().remove();
            }

            for (BlockVector3 vector : region) {
                Block block = world.getBlockAt(vector.getX(), vector.getY(), vector.getZ());
                this.provider.resolveSign(block);
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
