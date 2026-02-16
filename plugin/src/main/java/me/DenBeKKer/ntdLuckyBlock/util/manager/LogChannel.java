package me.DenBeKKer.ntdLuckyBlock.util.manager;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogChannel {

    private final Logger logger;
    private final Plugin plugin;
    private boolean debug = false;

    public LogChannel(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean setDebug(boolean debug) {
        return this.debug = debug;
    }

    public void debug(String message) {
        this.logger.log(Level.INFO, "[DEBUG] " + message);
    }

    public void info(String message) {
        this.logger.log(Level.INFO, message);
    }

    public void warning(String message) {
        this.logger.log(Level.WARNING, message);
    }

    public void severe(String message) {
        this.logger.log(Level.SEVERE, message);
    }

    public void debug(String message, Throwable throwable) {
        this.logger.log(Level.INFO, "[DEBUG] " + message, throwable);
    }

    public void info(String message, Throwable throwable) {
        this.logger.log(Level.INFO, message, throwable);
    }

    public void warning(String message, Throwable throwable) {
        this.logger.log(Level.WARNING, message, throwable);
    }

    public void severe(String message, Throwable throwable) {
        this.logger.log(Level.SEVERE, message, throwable);
    }
}
