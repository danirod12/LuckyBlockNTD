package com.github.danirod12.luckyblock.api.provider;

import com.github.danirod12.luckyblock.api.model.PluginVersion;
import com.github.danirod12.luckyblock.api.util.ISpigotUpdater;
import com.github.danirod12.luckyblock.api.util.LogChannel;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class LBMainProvider extends JavaPlugin {
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public abstract void reloadSystem();

    @Override
    public abstract void reloadConfig();

    public abstract LogChannel getLogChannel();

    public abstract ISpigotUpdater getSpigotUpdater();

    public abstract String getLastUpdate();

    public abstract PluginVersion getVersionType();
}
