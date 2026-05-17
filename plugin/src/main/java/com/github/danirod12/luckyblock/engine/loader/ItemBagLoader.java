package com.github.danirod12.luckyblock.engine.loader;

import com.github.danirod12.luckyblock.api.model.ItemsBag;
import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.api.model.random.Amount;
import com.github.danirod12.luckyblock.api.model.random.LuckyCollection;
import com.github.danirod12.luckyblock.api.util.Pair;
import com.github.danirod12.luckyblock.engine.drop.MessageDrop;
import com.github.danirod12.luckyblock.engine.model.ItemsBagImpl;
import com.github.danirod12.luckyblock.util.random.WeightListAmount;
import com.github.danirod12.luckyblock.util.random.WeightListB;
import com.github.danirod12.luckyblock.util.random.WeightListBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemBagLoader {

    private final LuckyDropLoader luckyDropLoader = new LuckyDropLoader();
    private final Map<String, Double> chanceLevels = new HashMap<>();

    public void setChanceLevels(Map<String, Double> weights) {
        this.chanceLevels.clear();
        this.chanceLevels.putAll(weights.entrySet().stream()
                .collect(Collectors.toMap(entry ->
                        entry.getKey().toUpperCase(), Map.Entry::getValue)));
    }

    /*
     * entries-mode: "ALL",number,number-number (default 1)
     * entries:
     *  'id':
     *      chance: "AUTO"/LEVEL/weight (optional, default AUTO)       // entry chance
     *      mode: "ALL",number,number-number (optional, default ALL)   // entry items rolling mode
     *      permission: "NONE",node (optional, default NONE)           // entry permission
     *      drops: (list-like)                                         // all items same chance
     *          - "item : nbt/data" (example)"
     *          - "other : other : other..."
     *      drops: (id-like)                                           // different chance for each item, permissions
     *          'id':
     *              chance: "AUTO"/LEVEL/weight (optional, default AUTO)
     *              permission: "NONE",node (optional, default NONE)
     *              node: "item : nbt/data"/"other : other : other..."
     */


    // TODO make V2 -> V3 conversion
//    public WeightListAmount<LuckyDrop> loadLuckyEntry(Config config, String path)
//            throws EntryFormatException, PremiumVersionRequiredException {
//        if (config == null || !config.isLoaded()) {
//            throw new IllegalArgumentException("Config is not loaded");
//        }
//
//        WeightListAmount<LuckyDrop> entry = null;
//        // If path contains items field, we are dealing with modern JSON loader
//        if (config.isSet(path + ".items")) {
//            // I am so sorry, but we hate hackers. I do not know why someone run free after premium
//            if (!LBMain.isPremium()) {
//                throw new PremiumVersionRequiredException("JSON loader");
//            }
//

//            DropChance chance = DropChance.MEDIUM;
//            try {
//                chance = DropChance.valueOf(config.getString(path + ".chance").toUpperCase());
//            } catch (Exception ignored) {
//            }
//            entry = new LuckyEntryHolder();
//            ConfigurationSection section = config.getConfigurationSection(path + ".items");
//            for (String key : section.getKeys(false)) {
//                try {
//                    LuckyDrop drop = this.pathLoader.load(config.getConfigurationSection(path + ".items." + key));
//                    if (drop != null) {
//                        entry.add(drop);
//                    }
//                } catch (Throwable throwable) {
//                    notifyMisconfiguration(config.getName(), path, "json", throwable);
//                }
//            }
//        } else if (config.isSet(path)) {
//            entry = new WeightListAmount<>();
//            List<String> list = config.getStringList(path);
//            for (String dropData : list) {
//                try {
//                    LuckyDrop drop = this.stringLoader.deserialize(dropData);
//                    if (drop != null) {
//                        entry.add(drop);
//                    }
//                } catch (Throwable throwable) {
//                    notifyMisconfiguration(config.getName(), path, "legacy", throwable);
//                }
//            }
//            this.convertFactory.add(config, path);
//        }
//        if (entry != null && entry.isEmpty()) {
//            this.logChannel.warning("Skipping entry with 0 items... " + config.getName() + ", " + path);
//            entry = null;
//        }
//        if (entry == null) {
//            throw new EntryFormatException(config.getName(), path);
//        }
//        return entry;
//    }

    public ItemsBag load(ConfigurationSection section) {
        WeightListBuilder<LuckyCollection<LuckyDrop>, String> builder = new WeightListBuilder<>();

        try {
            Object object = section.get("entries");
            if (object instanceof List<?>) { // [ - entries: ... ]
                List<?> list = (List<?>) object;
                for (Object entry : list) {
                    if (entry instanceof Map<?, ?>) {
                        ConfigurationSection entrySection = convertMapToSection((Map<?, ?>) entry);
                        this.loadEntry(builder, entrySection);
                    } else if (entry instanceof String) {
                        LuckyCollection<LuckyDrop> weightList = new WeightListAmount<>();
                        weightList.add(this.loadDrop((String) entry));
                        builder.addAuto(weightList, null);
                    }
                }
            } else if (object instanceof ConfigurationSection) { // [ entries: '0': ... ]
                ConfigurationSection entriesSection = (ConfigurationSection) object;
                for (String key : entriesSection.getKeys(false)) {
                    ConfigurationSection entrySection = entriesSection.getConfigurationSection(key);
                    if (entrySection != null) {
                        this.loadEntry(builder, entrySection);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ItemsBagImpl weightList = new ItemsBagImpl();
        weightList.setAmount(Amount.of(section.getString("entries-mode", "1")));
        builder.append(weightList);

        if (weightList.isEmpty()) {
            WeightListAmount<LuckyDrop> list = new WeightListAmount<>();
            list.add(new MessageDrop("§cError loading: Empty items bag"));
            weightList.add(list);
        }

        return weightList;
    }

    @SuppressWarnings("unchecked") // TODO make safer?
    public void write(ConfigurationSection section, ItemsBag bag) {
        section.set("entries-mode", bag.getAmount().toString());

        List<Map<String, Object>> keyValueEntries = new ArrayList<>();
        Map<LuckyCollection<LuckyDrop>, ?> entries = bag instanceof WeightListB
                ? ((WeightListB<LuckyCollection<LuckyDrop>, String>) bag).toWeightBindMap()
                : bag.toWeightMap();
        for (Map.Entry<LuckyCollection<LuckyDrop>, ?> entry : entries.entrySet()) {
            double chance = entry.getValue() instanceof Double
                    ? (double) entry.getValue() : ((Pair<Double, ?>) entry.getValue()).getA();
            String permission = entry.getValue() instanceof Pair ? ((Pair<?, String>) entry.getValue()).getB() : null;

            LuckyCollection<LuckyDrop> collection = entry.getKey();
            Amount mode = collection.getAmount();

            List<LuckyDropSave> dropSaves;
            if (collection instanceof WeightListB) {
                dropSaves = ((WeightListB<LuckyDrop, String>) collection).toWeightBindMap().entrySet()
                        .stream()
                        .map(e -> new LuckyDropSave(e.getValue().getKey(), e.getValue().getValue(),
                                this.luckyDropLoader.serialize(e.getKey()))).collect(Collectors.toList());
            } else {
                dropSaves = collection.toWeightMap().entrySet()
                        .stream()
                        .map(e -> new LuckyDropSave(e.getValue(), null,
                                this.luckyDropLoader.serialize(e.getKey())))
                        .collect(Collectors.toList());
            }

            double baseChance = dropSaves.get(0).chance;
            for (LuckyDropSave dropSave : dropSaves) {
                if (dropSave.permission != null || dropSave.chance != baseChance) {
                    baseChance = -1; // use rich-save format
                    break;
                }
            }

            Object drops;
            if (baseChance != -1) { // use lightweight format
                drops = dropSaves.stream().map(save -> save.serialized).collect(Collectors.toList());
            } else {
                List<Map<String, Object>> dropMaps = new ArrayList<>();
                for (LuckyDropSave dropSave : dropSaves) {
                    Map<String, Object> dropMap = new HashMap<>();
                    dropMap.put("chance", dropSave.chance);
                    if (dropSave.permission != null) {
                        dropMap.put("permission", dropSave.permission);
                    }
                    dropMap.put("node", dropSave.serialized);
                    dropMaps.add(dropMap);
                }
                drops = dropMaps;
            }
            keyValueEntries.add(new HashMap<String, Object>() {
                {
                    put("chance", chance);
                    if (permission != null) {
                        put("permission", permission);
                    }
                    put("mode", mode.toString());
                    put("drops", drops);
                }
            });
        }

        section.set("entries", keyValueEntries);
    }

    @AllArgsConstructor
    private static class LuckyDropSave {
        private final double chance;
        private final String permission;
        private final String serialized;
    }

    private void loadEntry(WeightListBuilder<LuckyCollection<LuckyDrop>, String> builder,
                           ConfigurationSection section) {
        /*
         * 'id':
         *  chance: "AUTO"/LEVEL/weight (optional, default AUTO)
         *  mode: "ALL",number,number-number (optional, default ALL)
         *  permission: "NONE",node (optional, default NONE)
         *  drops: (list-like)
         *      - "item : nbt/data" (example)
         *      - "other : other : other..."
         *  drops: (id-like)
         */

        WeightListAmount<LuckyDrop> entry = new WeightListAmount<>();
        entry.setAmount(Amount.of(section.getString("mode", "ALL")));

        Object drops = section.get("drops");

        if (drops instanceof List<?>) { // [ drops: - "item" ]
            List<?> list = (List<?>) drops;
            for (Object drop : list) {
                if (drop instanceof String) {
                    entry.add(this.loadDrop((String) drop));
                } else if (drop instanceof Map<?, ?>) {
                    ConfigurationSection dropSection = convertMapToSection((Map<?, ?>) drop);
                    LuckyDrop luckyDrop = this.loadDrop(dropSection.getString("node", ""));
                    if (luckyDrop != null) {
                        entry.add(luckyDrop, this.getWeight(dropSection), this.getPermission(dropSection));
                    }
                }
            }
        } else if (drops instanceof ConfigurationSection) { // [ drops: 'my_drop': { node: "item" } ]
            ConfigurationSection dropsSection = (ConfigurationSection) drops;
            for (String key : dropsSection.getKeys(false)) {
                Object dropObject = dropsSection.get(key);

                if (dropObject instanceof ConfigurationSection) {
                    ConfigurationSection dropSection = (ConfigurationSection) dropObject;
                    LuckyDrop luckyDrop = this.loadDrop(dropSection.getString("node", ""));
                    if (luckyDrop != null) {
                        entry.add(luckyDrop, this.getWeight(dropSection), this.getPermission(dropSection));
                    }
                } else if (dropObject instanceof String) {
                    entry.add(this.loadDrop((String) dropObject));
                }
            }
        }

        builder.add(entry, this.getWeight(section), this.getPermission(section));
    }

    private String getPermission(ConfigurationSection section) {
        String permission = section.getString("permission", null);
        if (permission != null && permission.equalsIgnoreCase("none")) {
            return null;
        }
        return permission;
    }

    private double getWeight(ConfigurationSection section) {
        String chance = section.getString("chance", "AUTO");
        double weight = 0;
        if (chance.equalsIgnoreCase("AUTO")) {
            weight = -1; // AUTO
        } else {
            try {
                weight = Double.parseDouble(chance);
            } catch (NumberFormatException ignored) {
            }
        }
        if (weight == 0) {
            weight = this.chanceLevels.getOrDefault(chance.toUpperCase(), -1.0D);
        }
        return weight;
    }

    private LuckyDrop loadDrop(String data) {
        try {
            return this.luckyDropLoader.deserialize(data);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new MessageDrop("§cError loading drop: " + data);
        }
    }

    private ConfigurationSection convertMapToSection(Map<?, ?> map) {
        MemoryConfiguration memoryConfig = new MemoryConfiguration();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();

            if (value instanceof Map<?, ?>) {
                memoryConfig.set(key, convertMapToSection((Map<?, ?>) value));
            } else if (value instanceof List<?>) {
                List<?> list = (List<?>) value;

                if (!list.isEmpty() && list.get(0) instanceof Map<?, ?>) {
                    MemoryConfiguration virtualSection = new MemoryConfiguration();
                    for (int i = 0; i < list.size(); i++) {
                        virtualSection.set(String.valueOf(i), convertMapToSection((Map<?, ?>) list.get(i)));
                    }
                    memoryConfig.set(key, virtualSection);
                } else {
                    memoryConfig.set(key, value);
                }
            } else {
                memoryConfig.set(key, value);
            }
        }
        return memoryConfig;
    }
}
