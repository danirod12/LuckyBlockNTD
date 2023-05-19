package me.DenBeKKer.ntdLuckyBlock.util;

import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface IWorldEdit {

    default void paste(File file, Block obj, boolean activate) {
        this.paste(file, obj, activate, new ArrayList<>());
    }

    void paste(File file, Block obj, boolean activate, List<String> blacklist);

}
