package com.github.danirod12.luckyblock.api.loader;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.random.LuckyCollection;
import org.bukkit.configuration.ConfigurationSection;

public interface PathLoader {
    LuckyDrop load(ConfigurationSection section) throws Exception;

    void save(ConfigurationSection section, String path, LuckyCollection<LuckyDrop> entry);
}
