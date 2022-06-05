package me.DenBeKKer.ntdLuckyBlock.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SpigotUpdater {

    private final int id;
    private final URL url;
    private final Plugin plugin;
    private final String friendly_name;
    private String version;
    private boolean need_update = false;

    public final static Pattern pattern = Pattern.compile("[0-9.]*");

    public SpigotUpdater(JavaPlugin plugin, int id) {
        this(plugin, id, null);
    }

    public SpigotUpdater(JavaPlugin plugin, int id, String friendly_name) {
        this.plugin = plugin;
        this.friendly_name = friendly_name == null ? plugin.getDescription().getName() : friendly_name;
        this.version = plugin.getDescription().getVersion();
        this.id = id;
        URL url = null;
        try {
            url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.url = url;
    }

    // Static methods
    public static boolean isNewer(String version, String origin) {
        return compareVersions(version, origin) > 0;
    }

    public static int compareVersions(String version, String origin) {
        final int[] a = prepare(version), b = prepare(origin);

        int A = 0, B = 0;
        for (int i = 0; i < a.length || i < b.length; i++) {

            for (int j = 1; j < String.valueOf(Math.max(i < a.length ? a[i] : 1, i < b.length ? b[i] : 1)).length(); j++) {
                A *= 10;
                B *= 10;
            }

            if (i < a.length) A += a[i];
            if (i < b.length) B += b[i];
            A *= 10;
            B *= 10;

        }
        return A - B;

    }

    public static int[] prepare(String version) {
        if (version.contains("-")) version = version.split("-")[0];
        version = version.replace("_", ".");
        if (!pattern.matcher(version).matches()) return new int[0];
        return Stream.of(version.split("\\.")).mapToInt(Integer::valueOf).toArray();
    }

    public int getProjectID() {
        return id;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean checkForUpdates() throws Exception {
        URLConnection con = url.openConnection();
        this.version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        need_update = !plugin.getDescription().getVersion().equalsIgnoreCase(version);
        return need_update;
    }

    public String getLatestVersion() {
        return version;
    }

    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + id;
    }

    public boolean need_update$cache() {
        return need_update;
    }

    public boolean checkForUpdatesFixed() throws Exception {
        checkForUpdates();
        need_update = isNewer(version, plugin.getDescription().getVersion());
        return need_update;
    }

    public void announce(Player target) {

        if (!need_update) return;
        if (target == null)
            Bukkit.getOnlinePlayers().forEach(n -> {
                if (n.hasPermission(plugin.getName().toLowerCase() + ".update")) a(n);
            });
        else a(target);

    }

    private void a(Player p) {
        p.sendMessage("\u00a76╔");
        p.sendMessage("\u00a76║   \u00a7c\u00a7l[!] \u00a7aNew plugin version for \u00a7e" + friendly_name
                + "\u00a7a has been released!");
        p.sendMessage("\u00a76║ \u00a7aYour current version is \u00a77" + plugin.getDescription().getVersion()
                + "\u00a7a. New version is \u00a7c" + version);
        p.sendMessage("\u00a76║ \u00a7aCheck \u00a7b" + getResourceURL() + "  \u00a76\u00a7l^_^");
        p.sendMessage("\u00a76╚");
    }

    public void check$announce(boolean print_exception, boolean inform) {

        try {
            if (checkForUpdatesFixed()) {

                ConsoleCommandSender logger = Bukkit.getConsoleSender();

                logger.sendMessage("[" + plugin.getName() + "] \u00a76╔");
                logger.sendMessage("[" + plugin.getName() + "] \u00a76║   \u00a7c\u00a7l[!] \u00a7aNew plugin version for \u00a7e" + friendly_name
                        + "\u00a7a has been released!");
                logger.sendMessage("[" + plugin.getName() + "] \u00a76║ \u00a7aYour current version is \u00a77" + plugin.getDescription().getVersion()
                        + "\u00a7a. New version is \u00a7c" + version);
                logger.sendMessage("[" + plugin.getName() + "] \u00a76║ \u00a7aCheck \u00a7b" + getResourceURL() + "  \u00a76\u00a7l^_^");
                logger.sendMessage("[" + plugin.getName() + "] \u00a76╚");
                if (inform) announce(null);

            }
        } catch (Exception e) {
            if (print_exception) {
                plugin.getLogger().log(Level.WARNING, "SpigotMC servers is unavailable... Check plugin page for updates " + getResourceURL());
                e.printStackTrace();
            }
        }

    }

}
