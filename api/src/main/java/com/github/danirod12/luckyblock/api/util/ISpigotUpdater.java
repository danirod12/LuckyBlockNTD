package com.github.danirod12.luckyblock.api.util;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface ISpigotUpdater {
    int getProjectID();

    Plugin getPlugin();

    boolean checkForUpdates() throws Exception;

    String getLatestVersion();

    String getResourceURL();

    boolean isNeedUpdate();

    void sendUpdateMessage(CommandSender target);

    void checkForUpdates(boolean notifyWebIssue, boolean inform);
}
