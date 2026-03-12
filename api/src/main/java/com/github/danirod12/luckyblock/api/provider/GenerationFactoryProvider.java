package com.github.danirod12.luckyblock.api.provider;

import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.LuckyEntry;
import com.github.danirod12.luckyblock.api.util.Config;

public interface GenerationFactoryProvider {
    void generateBaseData(Config config, LuckyBlockKey type);

    void saveLuckyEntries(Config config, LuckyEntry... entries);

    void saveUsingLoader(Config config, String path, LuckyEntry entry);

    LuckyEntry[] generateLuckyEntries(int min, int max);

    LuckyEntry generateLuckyEntry();

    LuckyDrop generateLuckyDrop();
}
