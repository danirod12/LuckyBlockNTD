package me.DenBeKKer.ntdLuckyBlock.api.provider;

import me.DenBeKKer.ntdLuckyBlock.api.model.PluginVersion;
import me.DenBeKKer.ntdLuckyBlock.api.util.ISpigotUpdater;
import me.DenBeKKer.ntdLuckyBlock.api.util.LogChannel;
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
