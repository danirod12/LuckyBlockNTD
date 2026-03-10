package me.DenBeKKer.ntdLuckyBlock.api.provider;

import me.DenBeKKer.ntdLuckyBlock.api.util.ISpigotUpdater;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class LBMainProvider extends JavaPlugin {
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public abstract void reloadSystem();

    @Override
    public abstract void reloadConfig();

    public abstract ISpigotUpdater getSpigotUpdater();
}
