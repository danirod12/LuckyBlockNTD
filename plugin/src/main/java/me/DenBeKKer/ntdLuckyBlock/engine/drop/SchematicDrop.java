package me.DenBeKKer.ntdLuckyBlock.engine.drop;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.loader.CustomSaver;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.WorldEditProvider;
import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;

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
     */
    public SchematicDrop(File file, boolean b, boolean i) {
        this.b = b;
        this.i = i;
        this.file = file;
    }

    @Deprecated
    public static LuckyDrop load(String description) {

        if (!Hook.WorldEdit.isEnabled()) {
            MvLogger.log(Level.WARNING, "WorldEdit was not found, lucky item \""
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
                MvLogger.log(Level.WARNING, "Schematic " + file.getPath() + " not found");
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
