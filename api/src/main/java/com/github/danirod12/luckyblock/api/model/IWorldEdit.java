package com.github.danirod12.luckyblock.api.model;

import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface IWorldEdit {

    default void paste(File file, Block obj, boolean ignoreAir) {
        this.paste(file, obj, ignoreAir, new ArrayList<>());
    }

    void paste(File file, Block obj, boolean ignoreAir, List<String> blacklist);
}
