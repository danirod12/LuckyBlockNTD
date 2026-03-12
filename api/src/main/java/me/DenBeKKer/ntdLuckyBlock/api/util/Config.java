package me.DenBeKKer.ntdLuckyBlock.api.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class Config extends YamlConfiguration {
    private final File folder;
    private final String name, resourceURL;
    private final Plugin plugin;
    @Setter
    private boolean hasChanges = false;
    private boolean loaded = false;

    private FileConfiguration resourceConf;

    public Config(File folder, String name) {
        this(null, null, folder, name);
    }

    public Config(Plugin plugin, File folder, String name) {
        this(plugin, null, folder, name);
    }

    public Config(Plugin plugin, String resourceURL, File folder, String name) {
        Objects.requireNonNull(name, "You need to provide a config name");
        if ((folder == null || resourceURL != null) && plugin == null) {
            throw new IllegalArgumentException("You need to provide a plugin instance for this action");
        }

        this.folder = folder == null ? plugin.getDataFolder() : folder;
        this.name = name.contains(".") ? name : name + ".yml";

        if (resourceURL != null) {
            resourceURL = resourceURL.replace(".", "/");
            if (!resourceURL.endsWith("/")) {
                resourceURL += "/";
            }
        }
        this.resourceURL = resourceURL;
        this.plugin = plugin;
    }

    public File getFile() {
        return new File(folder, name);
    }

    public InputStream getResourceInputStream() {
        if (plugin == null) {
            throw new UnsupportedOperationException("Instance not provided to copy resource. Use \"#write\" method");
        }
        return plugin.getResource((resourceURL == null ? "" : resourceURL) + name);
    }

    @Deprecated
    public FileConfiguration get() {
        return this;
    }

    public Config createFile() {
        File target = getFile();
        try {
            File folder = target.getParentFile();
            if (!folder.exists() && folder.isDirectory()) {
                folder.mkdirs();
            }
            if (!target.exists()) {
                target.createNewFile();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    @Deprecated
    public Config copy() {
        return copy(true);
    }

    public Config copy(boolean load) {
        folder.mkdirs();
        File file = getFile();
        if (!file.exists()) {
            try {
                Files.copy(getResourceInputStream(), file.toPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (load) {
            load();
        }
        return this;
    }

    public Config save() {
        try {
            save(getFile());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return this;
    }

    @Override
    public void save(File target) throws IOException {
        super.save(target);
        this.hasChanges = false;
    }

    @Override
    public void save(String file) throws IOException {
        super.save(file);
        this.hasChanges = false;
    }

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        super.load(file);
        this.hasChanges = false;
        this.loaded = true;
    }

    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        super.load(reader);
        this.hasChanges = false;
        this.loaded = true;
    }

    @Override
    public void load(String file) throws IOException, InvalidConfigurationException {
        super.load(file);
        this.hasChanges = false;
        this.loaded = true;
    }

    public boolean hasResource() {
        return getFile().exists() || (plugin != null && getResourceInputStream() != null);
    }

    public Config load() {
        try {
            super.load(getFile());
            this.hasChanges = false;
        } catch (IOException | InvalidConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public void delete() {
        delete(true);
    }

    public void delete(boolean folder) {
        File file = getFile();
        if (file.exists()) {
            do {
                file.delete();
                if ((file = file.getParentFile()) == null) {
                    break;
                }
            } while (folder && file.isDirectory() && file.listFiles().length == 0);
        }
    }

    public boolean hasChanges() {
        return this.hasChanges;
    }

    // Configuration copy to load new language entries, etc

    public FileConfiguration getResourceConf() {
        return getResourceConf(null, false);
    }

    public FileConfiguration getResourceConf(boolean reload) {
        return getResourceConf(null, reload);
    }

    public FileConfiguration getResourceConf(InputStream stream, boolean reload) {
        if (this.resourceConf != null && !reload) {
            return this.resourceConf;
        }
        if (stream == null) {
            stream = getResourceInputStream();
        }
        try {
            this.resourceConf = new YamlConfiguration();
            this.resourceConf.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            return this.resourceConf;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public double getDouble(String path, double min, double max) {
        double d = getDouble(path);
        return d < min ? min : max < min || d < max ? d : max;
    }

    public int getInt(String path, int min, int max) {
        int d = getInt(path);
        return d < min ? min : max < min || d < max ? d : max;
    }

    public <T> List<T> getList(String path, Function<Pair<String, ConfigurationSection>, T> function) {
        List<T> list = new ArrayList<>();
        ConfigurationSection section = this.getConfigurationSection(path);
        for (String element : section.getKeys(false)) {
            T t = function.apply(new Pair<>(element, section.getConfigurationSection(element)));
            if (t == null) {
                continue;
            }
            list.add(t);
        }
        return list;
    }

    public <T> void saveList(String path, List<T> list, Function<T, Map<?, ?>> save,
                             Function<T, String> nameGenerator) {
        if (nameGenerator == null) {
            Single<Integer> single = new Single<>(0);
            nameGenerator = t -> {
                int value = single.get();
                single.set(value + 1);
                return String.valueOf(value);
            };
        }
        this.set(path, null);
        int id = 0;
        for (T element : list) {
            String field = name == null ? String.valueOf(id++) : nameGenerator.apply(element);
            this.createSection(path + "." + field, save.apply(element));
        }
        this.hasChanges = true;
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
        if (!this.loaded) {
            this.copy(true);
        }

        skip = skip.stream().map(String::toLowerCase).collect(Collectors.toSet());

        getResourceConf(); // load this.resourceConf
        keys:
        for (String key : this.resourceConf.getKeys(true)) {
            for (String string : skip) {
                if (key.toLowerCase().startsWith(string)) {
                    continue keys;
                }
            }
            if (!this.isSet(key)) {
                this.set(key, this.resourceConf.get(key));
                if (callback != null) {
                    callback.accept(key);
                }
            }
        }
        if (this.hasChanges()) {
            this.save();
        }
        return this;
    }

    @Override
    public void set(String path, Object value) {
        super.set(path, value);
        this.hasChanges = true;
    }
}
