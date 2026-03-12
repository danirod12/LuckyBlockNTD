package me.DenBeKKer.ntdLuckyBlock.api.util;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogChannel {

    private final Logger logger;
    @Getter
    private final Plugin plugin;
    @Getter
    private boolean debug = false;

    public LogChannel(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
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

    public void log(Level level, String text) {
        this.logger.log(level, text);
    }
}
