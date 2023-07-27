package me.DenBeKKer.ntdLuckyBlock.variables.drop;

import com.google.gson.annotations.SerializedName;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.api.loader.CustomSaver;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.LBWorldEdit;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

public class SchematicDrop implements LuckyDrop, CustomSaver {

    @SerializedName(value = "block")
    private final boolean b;
    @SerializedName(value = "air")
    private final boolean i;
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
            LBMain.getInstance().getLogger().log(Level.WARNING, "WorldEdit was not found, lucky item \""
                    + description + "\" wont be loaded");
            throw new UnsupportedOperationException();
        }

        boolean b = Boolean.parseBoolean(description.split(" : ")[1]);

        String schematic = description.split(" : ")[0];
        String file$name = schematic.endsWith(".schem") ? schematic : schematic + ".schem";

        File file = new File(LBMain.getSchematicsFolder(), file$name);
        if (!file.exists()) {

            file$name = schematic.endsWith(".schematic") ? schematic : schematic + ".schematic";
            file = new File(LBMain.getSchematicsFolder(), file$name);

            if (!file.exists()) {
                LBMain.getInstance().getLogger().log(Level.WARNING, "Schematic " + file.getPath() + " not found");
                return null;
            }

        }

        return new SchematicDrop(file, b);

    }

    public File getFile() {
        return file;
    }

    public boolean atBlock() {
        return b;
    }

    @Override
    public void execute(LBMain.LuckyBlockType related, Block b, Player target) {
        LBWorldEdit.paste(file, this.b || target == null ? b : target.getLocation().getBlock(), i);
    }

    @Override
    public String getDescription() {
        return file.getName() + " : " + (b ? "true" : "false");
    }

}
