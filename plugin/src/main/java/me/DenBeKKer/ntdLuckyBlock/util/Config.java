package me.DenBeKKer.ntdLuckyBlock.util;

import com.google.common.collect.Maps;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final File folder;
    private final String name, resource;
    private final Plugin plugin;

    private FileConfiguration config;

    public Config(File folder, String name) {
        this(null, null, folder, name);
    }

    public Config(Plugin plugin, File folder, String name) {
        this(plugin, null, folder, name);
    }

    public Config(Plugin plugin, String resource, File folder, String name) {

        if(name == null) throw new IllegalArgumentException("You need to provide a config name");
        if((folder == null || resource != null) && plugin == null)
            throw new IllegalArgumentException("You need to provide a plugin instance for this action");

        this.folder = folder == null ? plugin.getDataFolder() : folder;
        this.name = name.contains(".") ? name : name + ".yml";

        if(resource != null) {
            resource = resource.replace(".", "/");
            if(!resource.endsWith("/")) resource += "/";
        }
        this.resource = resource;
        this.plugin = plugin;

    }

    @Override
    public String toString() {
        return "Config{plugin=" + (plugin == null ? "null" : plugin.getName()) + ",resource=" + (resource == null ? "null" : resource) +
                ",folder=" + (folder == null ? "null" : folder.toString()) + ",name=" + name + "}";
    }

    public File getFolder() { return folder; }

    public File getFile() { return new File(folder, name); }

    public String getName() { return name; }

    public String getResourceURL() { return resource; }

    public InputStream getResource() {
        if(plugin == null) throw new UnsupportedOperationException("Instance not provided to copy resource. Use \"#write\" method");
        return plugin.getResource((resource == null ? "" : resource) + name);
    }

    public Plugin getPlugin() { return plugin; }

    public FileConfiguration get() { return config; }

    public FileConfiguration getSafe() {
        if(config == null) load();
        return config;
    }

    public Config write() { return write(getFile()); }

    public Config write(File target) {
        try {
            File folder = target.getParentFile();
            if(!folder.exists() && folder.isDirectory()) folder.mkdirs();
            if(!target.exists()) target.createNewFile();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Deprecated
    public Config copy() { return copy(true); }

    public Config copy(boolean load) {

        folder.mkdirs();
        File file = getFile();
        if(!file.exists()) {
            try {
                Files.copy(getResource(), file.toPath(), new CopyOption[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
                return this;
            }
        }
        if(load) load();
        return this;

    }

    public Config save() { return save(getFile()); }

    public Config save(File target) {
        if(config == null) throw new UnsupportedOperationException("Config not loaded. Use \"#load\" first");
        try {
            if(!folder.exists()) folder.mkdirs();
            if (!target.exists()) write(target);
            config.save(target);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public boolean hasResource() { return getFile().exists() || (plugin != null && getResource() != null); }

    public Config reload() { return load(); }

    public Config load() {
        try {
            config = new YamlConfiguration();
            config.load(getFile());
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public void delete() { delete(true); }

    public void delete(boolean folder) {

        File file = getFile();
        if(file.exists()) {
            do {
                file.delete();
                file = file.getParentFile();
            } while(folder && file.isDirectory() && file.listFiles().length == 0);
        }

    }

    // Configuration copy to load new language entries, etc
    private FileConfiguration def;

    public FileConfiguration getDefault() { return getDefault(null, false); }

    public FileConfiguration getDefault(boolean reload) { return getDefault(null, reload); }

    public FileConfiguration getDefault(InputStream stream, boolean reload) {
        if(def != null && !reload) return def;
        if(stream == null) stream = getResource();
        try {
            def = new YamlConfiguration();
            def.load(new InputStreamReader(stream, "UTF-8"));
            return def;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void gc(boolean origin, boolean copy) {
        if(origin) config = null;
        if(copy) def = null;
    }

    // Some methods from FileConfiguration
    public void set(String path, Object object) {
        config.set(path, object);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getDouble(String path, int min, int max) {
        double d = getDouble(path);
        return d < min ? min : max < min || d < max ? d : max;
    }

    public float getFloat(String path) {
        return (float)getDouble(path);
    }

    public boolean getBoolean(String path) { return config.getBoolean(path); }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    // Smart collectionable list storing
    public <T> List<T> getList(String path, Function<Map.Entry<FileConfiguration, String>, T> function) {
        List<T> list = new ArrayList<>();
        for(String element : config.getConfigurationSection(path).getKeys(false)) {
            T t = function.apply(Maps.immutableEntry(config, path + "." + element));
            if(t == null) continue;
            list.add(t);
        }
        return list;
    }

    public <T> void saveList(String path, List<T> list, Function<T, String> name, Function<T, HashMap<String, Object>> save) {
        config.set(path, null);
        int id = 0;
        for(T element : list) {
            String field = name == null ? String.valueOf(id++) : name.apply(element);
            for(Map.Entry<String, Object> entry : save.apply(element).entrySet()) {
                config.set(path + "." + field + "." + entry.getKey(), entry.getValue());
            }
        }
    }

    public Config copyMissedFields() {
        return this.copyMissedFields(new ArrayList<>(), null);
    }

    public Config copyMissedFields(Consumer<String> callback) {
        return this.copyMissedFields(new ArrayList<>(), callback);
    }

    public Config copyMissedFields(Collection<String> skip) {
        return copyMissedFields(skip, null);
    }

    public Config copyMissedFields(Collection<String> skip, Consumer<String> callback) {

        if(this.config == null) this.copy(true);

        skip = skip.stream().map(String::toLowerCase).collect(Collectors.toList());

        boolean need_save = false;
        final YamlConfiguration yml_default = ((YamlConfiguration) getDefault());
        for(String key : yml_default.getKeys(true)) {
            for(String string : skip)
                if(key.toLowerCase().startsWith(string)) continue;
            if (!this.config.isSet(key)) {
                this.config.set(key, yml_default.get(key));
                if(callback != null) callback.accept(key);
                need_save = true;
            }
        }
        if(need_save) this.save();
        return this;

    }

}
