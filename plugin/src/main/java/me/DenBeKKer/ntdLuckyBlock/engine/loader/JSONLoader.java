package me.DenBeKKer.ntdLuckyBlock.engine.loader;

import me.DenBeKKer.ntdLuckyBlock.api.exception.PremiumVersionRequiredException;
import me.DenBeKKer.ntdLuckyBlock.api.loader.PathLoader;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import org.bukkit.configuration.ConfigurationSection;

public class JSONLoader implements PathLoader {

    public JSONLoader(LuckyBlockEngine engine) {
    }

    /*
     *
     *  drops:
     *    '0':
     *      chance: HIGH
     *      items:
     *        '0':
     *          class: "ItemDrop"
     *          json: "{...}"
     *        '1':
     *          class: "ItemDrop"
     *          json: "{...}"
     *
     */

    @Override
    public LuckyDrop load(ConfigurationSection config) throws Exception {
        throw new PremiumVersionRequiredException("JSON loader");
    }

    @Override
    public void save(ConfigurationSection config, String path, LuckyEntry entry) {
        throw new PremiumVersionRequiredException("JSON loader");
    }
}
