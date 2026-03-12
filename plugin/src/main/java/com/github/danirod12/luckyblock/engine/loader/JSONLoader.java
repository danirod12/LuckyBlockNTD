package com.github.danirod12.luckyblock.engine.loader;

import com.github.danirod12.luckyblock.api.exception.PremiumVersionRequiredException;
import com.github.danirod12.luckyblock.api.loader.PathLoader;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.LuckyEntry;
import com.github.danirod12.luckyblock.engine.LuckyBlockEngine;
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
