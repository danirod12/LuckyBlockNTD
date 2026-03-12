package me.DenBeKKer.ntdLuckyBlock.api.loader;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;
import org.bukkit.configuration.ConfigurationSection;

public interface PathLoader {
    LuckyDrop load(ConfigurationSection section) throws Exception;

    void save(ConfigurationSection section, String path, LuckyEntry entry);
}
