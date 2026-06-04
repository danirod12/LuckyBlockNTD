package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.LBMain;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import lombok.Getter;
import org.bukkit.block.Block;

import java.io.File;

/**
 * TODO for V2p -> V3 add JSON support
 */
public class SchematicDrop implements LuckyDrop {
    @Getter
    private final File file;
    private final SchematicType type;
    private final boolean ignoreAir;

    public SchematicDrop(File file, SchematicType type) {
        this(file, type, false);
    }

    /**
     * @param file      schematic file
     * @param type      paste type
     * @param ignoreAir whether to ignore air blocks when pasting
     */
    public SchematicDrop(File file, SchematicType type, boolean ignoreAir) {
        this.file = file;
        this.type = type;
        this.ignoreAir = ignoreAir;
    }

    public enum SchematicType {
        BLOCK_RELATIVE,
        PLAYER_RELATIVE,

        ;

        public static SchematicType parse(String type) {
            return type != null && (type.contains("player") || type.equalsIgnoreCase("false"))
                    ? PLAYER_RELATIVE : BLOCK_RELATIVE;
        }

        @Override
        public String toString() {
            return this == BLOCK_RELATIVE ? "BLOCK_RELATIVE" : "PLAYER_RELATIVE"; // Obfuscation prevention
        }
    }

//    @Deprecated
//    public static LuckyDrop load(String description) {
//
//        if (!Hook.WorldEdit.isEnabled()) {
//            LuckyBlockAPI.getLogger().log(Level.WARNING, "WorldEdit was not found, lucky item \""
//                    + description + "\" wont be loaded");
//            throw new UnsupportedOperationException();
//        }
//
//        boolean b = Boolean.parseBoolean(description.split(" : ")[1]);
//
//        String schematic = description.split(" : ")[0];
//        String fileName = schematic.endsWith(".schem") ? schematic : schematic + ".schem";
//
//        File file = new File(WE_PROVIDER.get().getFolder(), fileName);
//        if (!file.exists()) {
//            fileName = schematic.endsWith(".schematic") ? schematic : schematic + ".schematic";
//            file = new File(WE_PROVIDER.get().getFolder(), fileName);
//
//            if (!file.exists()) {
//                LuckyBlockAPI.getLogger().log(Level.WARNING, "Schematic " + file.getPath() + " not found");
//                return null;
//            }
//        }
//        return new SchematicDrop(file, b);
//    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        Block block = this.type == SchematicType.BLOCK_RELATIVE || execution.getPlayer() == null
                ? execution.getBlock() : execution.getPlayer().getLocation().getBlock();
        LBMain.getPlugin(LBMain.class).getWorldEditProvider().paste(file, block, this.ignoreAir); // TODO rework this
        // TODO !!! REWORK THAT SHIT ACCESS
        // TODO !!!
        // TODO !!!
        // TODO !!!
        // TODO !!!
    }

    public static LuckyDrop deserialize(String[] data) {
        if (data.length < 1) {
            return null;
        }

        File folder = new File(LuckyBlockAPI.getInstance().getDataFolder(), "schematics");
        if (!folder.exists()) {
            folder.mkdirs();
            return null;
        }

        for (File file : new File[]{new File(folder, data[0]),
                new File(folder, data[0] + ".schem"),
                new File(folder, data[0] + ".schematic")}) {
            if (file.exists()) {
                return new SchematicDrop(file, data.length >= 2 ? SchematicType.parse(data[1])
                        : SchematicType.BLOCK_RELATIVE, data.length >= 3 && Boolean.parseBoolean(data[2]));
            }
        }
        return null;
    }

    public static String[] serialize(SchematicDrop drop) {
        return new String[]{drop.file.getName(), drop.type.toString(), Boolean.toString(drop.ignoreAir)};
    }

    @Deprecated
    public boolean atBlock() {
        return this.type.toString().contains("BLOCK");
    }
}
