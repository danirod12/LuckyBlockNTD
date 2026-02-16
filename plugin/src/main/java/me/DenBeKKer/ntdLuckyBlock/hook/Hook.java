package me.DenBeKKer.ntdLuckyBlock.hook;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Hook {

    Vault,
    TokenManager,
    WorldEdit,
    WorldGuard,
    SlimeFun;

    private String error = "not found";

    public static void loadAll() {
        for (Hook value : Hook.values()) {
            value.load();
        }
    }

    public static void print(Logger logger) {
        List<String> disabled = new ArrayList<>();
        for (Hook hook : Hook.values()) {
            if (hook.error == null) {
                logger.log(Level.INFO, ChatColor.GREEN + hook.name() + " connected");
            } else {
                disabled.add(ChatColor.RED + hook.name() + " " + hook.error);
            }
        }

        for (String message : disabled) {
            logger.log(Level.INFO, message);
        }

        logger.log(Level.INFO, ChatColor.GOLD + "Found " + (Hook.values().length - disabled.size())
                + "/" + Hook.values().length + " compatible plugins");
    }

    public static Hook parse(String name) {
        for (Hook hook : values()) {
            if (hook.name().equalsIgnoreCase(name)) {
                return hook;
            }
        }
        return null;
    }

    public void disable(String reason) {
        this.error = reason;
    }

    public boolean isEnabled() {
        return error == null;
    }

    public void load() {
        if (Bukkit.getPluginManager().isPluginEnabled(this.name())) {
            error = null;
        }
    }
}
