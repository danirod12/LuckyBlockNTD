package me.DenBeKKer.ntdLuckyBlock.util;

import me.DenBeKKer.ntdLuckyBlock.api.util.ISpigotUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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

public class SpigotUpdater implements ISpigotUpdater {

    private final int id;
    private final URL url;
    private final Plugin plugin;
    private final String friendlyName;
    private String version;
    private boolean needUpdate = false;

    public final static Pattern pattern = Pattern.compile("[0-9.]*");

    public SpigotUpdater(JavaPlugin plugin, int id) {
        this(plugin, id, null);
    }

    public SpigotUpdater(JavaPlugin plugin, int id, String friendlyName) {
        this.plugin = plugin;
        this.friendlyName = friendlyName == null ? plugin.getDescription().getName() : friendlyName;
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

    @Override
    public int getProjectID() {
        return id;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean checkForUpdates() throws Exception {
        URLConnection con = url.openConnection();
        this.version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        needUpdate = isNewer(version, plugin.getDescription().getVersion());
        return needUpdate;
    }

    @Override
    public String getLatestVersion() {
        return version;
    }

    @Override
    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + id;
    }

    @Override
    public boolean isNeedUpdate() {
        return needUpdate;
    }

    @Override
    public void sendUpdateMessage(CommandSender target) {
        if (!needUpdate) {
            return;
        }
        if (target == null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission(plugin.getName().toLowerCase() + ".update")) {
                    notify(onlinePlayer);
                }
            }
            notify(Bukkit.getConsoleSender());
        } else {
            notify(target);
        }
    }

    private void notify(CommandSender sender) {
        sender.sendMessage("§6╔");
        sender.sendMessage("§6║   §c§l[!] §aNew plugin version for §e" + friendlyName + "§a has been released!");
        sender.sendMessage("§6║ §aYour current version is §7" + plugin.getDescription().getVersion()
                + "§a. New version is §c" + version);
        sender.sendMessage("§6║ §aCheck §b" + getResourceURL() + "  §6§l^_^");
        sender.sendMessage("§6╚");
    }

    @Override
    public void checkForUpdates(boolean notifyWebIssue, boolean inform) {
        try {
            if (checkForUpdates() && inform) {
                sendUpdateMessage(null);
            }
        } catch (Exception exception) {
            if (notifyWebIssue) {
                plugin.getLogger().log(Level.WARNING, "SpigotMC servers is unavailable... " +
                        "Check plugin page for updates " + getResourceURL());
                exception.printStackTrace();
            }
        }
    }

    // Static methods
    private boolean isNewer(String version, String origin) {
        return compareVersions(version, origin) > 0;
    }

    private int compareVersions(String version, String origin) {
        final int[] a = prepare(version), b = prepare(origin);

        int A = 0, B = 0;
        for (int i = 0; i < a.length || i < b.length; i++) {
            int length = String.valueOf(Math.max(i < a.length ? a[i] : 1, i < b.length ? b[i] : 1)).length();
            for (int j = 1; j < length; j++) {
                A *= 10;
                B *= 10;
            }

            if (i < a.length)
                A += a[i];
            if (i < b.length)
                B += b[i];
            A *= 10;
            B *= 10;
        }
        return A - B;
    }

    private int[] prepare(String version) {
        if (version.contains("-")) version = version.split("-")[0];
        version = version.replace("_", ".");
        if (!SpigotUpdater.pattern.matcher(version).matches()) return new int[0];
        return Stream.of(version.split("\\.")).mapToInt(Integer::valueOf).toArray();
    }
}
