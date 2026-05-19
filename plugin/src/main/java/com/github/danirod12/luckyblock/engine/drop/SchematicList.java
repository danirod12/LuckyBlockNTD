package com.github.danirod12.luckyblock.engine.drop;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SchematicList {

    public static class SchematicData {
        public final String fileName;
        public final SchematicDrop.SchematicType type;
        public final int rarityWeight;
        public final boolean ignoreAir;

        public SchematicData(String fileName, SchematicDrop.SchematicType type, int rarityWeight, boolean ignoreAir) {
            this.fileName = fileName;
            this.type = type;
            this.rarityWeight = rarityWeight;
            this.ignoreAir = ignoreAir;
        }
    }

    public static final List<SchematicData> SCHEMATICS = Arrays.asList(
            new SchematicData("bedrock_problem.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 15, true),
            new SchematicData("cage_lava.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 15, false),
            new SchematicData("small_temple.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 15, true),
            new SchematicData("1_8n1.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_8n2.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_8n3.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_8n4.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_8n5.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_13n1.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_13n2_b.schem", SchematicDrop.SchematicType.BLOCK_RELATIVE, 20, true),
            new SchematicData("1_13n3.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_13n4.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_13n5.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("1_13n6.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("AncientPagoda.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("ApocalypsysHouse.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("BlossomTree.schem", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("FlameShrine.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("LavaGuardian.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true),
            new SchematicData("WatchTower.schematic", SchematicDrop.SchematicType.PLAYER_RELATIVE, 20, true)
    );

    public static void copyDefaults(Plugin plugin) {
        File schemFolder = new File(plugin.getDataFolder(), "schematics");
        if (!schemFolder.exists()) {
            schemFolder.mkdirs();
        }

        for (SchematicData data : SCHEMATICS) {
            File outFile = new File(schemFolder, data.fileName);
            if (!outFile.exists()) {
                try {
                    plugin.saveResource("schematics/" + data.fileName, false);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("No schematic found!");
                }
            }
        }
    }

    public static SchematicData getRandomSchematic() {
        if (SCHEMATICS.isEmpty()) {
            return null;
        }

        int totalWeight = SCHEMATICS.stream().mapToInt(s -> s.rarityWeight).sum();
        int roll = ThreadLocalRandom.current().nextInt(totalWeight);

        for (SchematicData data : SCHEMATICS) {
            roll -= data.rarityWeight;
            if (roll < 0) {
                return data;
            }
        }
        return SCHEMATICS.get(0);
    }
}
