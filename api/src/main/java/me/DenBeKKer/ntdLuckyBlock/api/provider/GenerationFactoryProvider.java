package me.DenBeKKer.ntdLuckyBlock.api.provider;

import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlockKey;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyEntry;
import me.DenBeKKer.ntdLuckyBlock.api.util.Config;

public interface GenerationFactoryProvider {
    void generateBaseData(Config config, LuckyBlockKey type);

    void saveLuckyEntries(Config config, LuckyEntry... entries);

    void saveUsingLoader(Config config, String path, LuckyEntry entry);

    LuckyEntry[] generateLuckyEntries(int min, int max);

    LuckyEntry generateLuckyEntry();

    LuckyDrop generateLuckyDrop();
}
