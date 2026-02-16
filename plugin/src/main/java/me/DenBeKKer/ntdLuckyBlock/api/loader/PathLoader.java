package me.DenBeKKer.ntdLuckyBlock.api.loader;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.util.Config;

public interface PathLoader {

    LuckyDrop load(Config loaded, String path) throws Exception;

    void save(Config config, String path, LuckyEntry entry);
}
