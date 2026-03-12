package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.LBMain;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.loader.CustomSaver;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.hook.Hook;
import com.github.danirod12.luckyblock.hook.sk89q.WorldEditProvider;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Level;

// TODO rework
public class SchematicDrop implements CustomSaver, LuckyDrop {

    private static final Supplier<WorldEditProvider> WE_PROVIDER =
            () -> ((LBMain) LuckyBlockAPI.getInstance()).getWorldEditProvider(); // TODO rework

    @SerializedName(value = "block")
    private final boolean b;
    @SerializedName(value = "air")
    private final boolean i;
    @Getter
    @SerializedName(value = "file")
    private final File file;

    public SchematicDrop(File file, boolean b) {
        this(file, b, false);
    }

    /**
     * @param file - Schematic file
     * @param b    - Place at block (true); Place at player (false)
     * @param i    - Replace air (true); Don't replace air (false)
     */
    public SchematicDrop(File file, boolean b, boolean i) {
        this.b = b;
        this.i = i;
        this.file = file;
    }

    @Deprecated
    public static LuckyDrop load(String description) {

        if (!Hook.WorldEdit.isEnabled()) {
            LuckyBlockAPI.getLogger().log(Level.WARNING, "WorldEdit was not found, lucky item \""
                    + description + "\" wont be loaded");
            throw new UnsupportedOperationException();
        }

        boolean b = Boolean.parseBoolean(description.split(" : ")[1]);

        String schematic = description.split(" : ")[0];
        String fileName = schematic.endsWith(".schem") ? schematic : schematic + ".schem";

        File file = new File(WE_PROVIDER.get().getFolder(), fileName);
        if (!file.exists()) {
            fileName = schematic.endsWith(".schematic") ? schematic : schematic + ".schematic";
            file = new File(WE_PROVIDER.get().getFolder(), fileName);

            if (!file.exists()) {
                LuckyBlockAPI.getLogger().log(Level.WARNING, "Schematic " + file.getPath() + " not found");
                return null;
            }
        }
        return new SchematicDrop(file, b);
    }

    @Override
    public void execute(LuckyDrop.Execution execution) {
        WE_PROVIDER.get().paste(file, this.b || execution.getPlayer() == null
                ? execution.getBlock() : execution.getPlayer().getLocation().getBlock(), i);
    }

    @Override
    public String getDescription() {
        return file.getName() + " : " + (b ? "true" : "false");
    }

    public boolean atBlock() {
        return b;
    }
}
