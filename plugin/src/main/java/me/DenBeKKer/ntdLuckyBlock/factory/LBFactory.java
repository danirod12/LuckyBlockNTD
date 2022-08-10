package me.DenBeKKer.ntdLuckyBlock.factory;

import me.DenBeKKer.ntdLuckyBlock.LBMain.LuckyBlockType;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Level;

public interface LBFactory {

    int latest_version = 2;

    static LBFactory latest() {

        try {
            return get(latest_version);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    static LBFactory get(int version) throws ClassNotFoundException {

        switch (version) {
            case 1:
                return new LBFactoryV1();
            case 2:
                return new LBFactoryV2();
            default:
                throw new ClassNotFoundException("Factory version " + version + " not found. Latest version is " + latest_version);
        }

    }

    @Deprecated
    default void generate(FileConfiguration file, LuckyBlockType type) {
        this.generate(file, type, 5, 50);
    }

    @Deprecated
    default void generate(FileConfiguration file, LuckyBlockType type, int min, int max) {
        this.generate(new Config(file), type, min, max);
    }

    default void generate(Config config, LuckyBlockType type, int min, int max) {
        Bukkit.getLogger().log(Level.WARNING, "Using legacy generator method...");
        this.generate(config.get(), type, min, max);
    }

}
