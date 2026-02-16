package me.DenBeKKer.ntdLuckyBlock.api.provider;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class JPAdapter extends JavaPlugin {
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public abstract void reloadSystem();

    @Override
    public abstract void reloadConfig();

    public abstract void checkForUpdates(boolean inform);
}
