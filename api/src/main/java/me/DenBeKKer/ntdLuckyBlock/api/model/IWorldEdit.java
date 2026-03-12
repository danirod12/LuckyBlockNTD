package me.DenBeKKer.ntdLuckyBlock.api.model;

import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface IWorldEdit {

    default void paste(File file, Block obj, boolean activate, boolean ignoreAir) {
        this.paste(file, obj, activate, ignoreAir, new ArrayList<>());
    }

    void paste(File file, Block obj, boolean activate, boolean ignoreAir, List<String> blacklist);
}
