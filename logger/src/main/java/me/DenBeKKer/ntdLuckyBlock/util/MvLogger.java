package me.DenBeKKer.ntdLuckyBlock.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MvLogger {

    private static Plugin instance;

    public static void setInstance(JavaPlugin plugin) {
        instance = plugin;
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        if (level == Level.INFO) {
            Bukkit.getConsoleSender().sendMessage("[ntdLuckyBlock] " + message.replace("&", "\u00a7"));
        } else {
            instance.getLogger().log(level, message.replace("&", "\u00a7"));
        }
    }

    public static Plugin getInstance() {
        return instance;
    }
}
