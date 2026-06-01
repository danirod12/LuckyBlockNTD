package com.github.danirod12.luckyblock.engine.generator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Getter
public class AdvancedLootDatabase {
    private int defaultBaseWeight = 10;
    private int defaultTier = 1;
    private double entitySpawnChance = 0.1;
    private double entityDropWeight = 30.0;
    private double specialSpawnChance = 0.1;
    private double specialDropWeight = 25.0;
    private double maxSimilarity = 0.7;
    private int topSimilar = 10;

    private SynergyMode defaultMode = SynergyMode.STRICT;
    private double semistrictModeWeight = 10.0;
    private double v2ModeWeight = 50.0;

    private final Map<XMaterial, CoreItem> cache = new HashMap<>();
    private final NavigableMap<Integer, XMaterial> weightedCores = new TreeMap<>();
    private int totalCoreWeight = 0;

    private final List<String> singleOnlyTags = new ArrayList<>();
    private final Set<String> allUniqueTags = new HashSet<>();
    private final Set<XMaterial> bannedItems = new HashSet<>();
    private final Set<EntityType> bannedEntities = new HashSet<>();
    private final Map<String, Integer> tagModifiers = new HashMap<>();
    private final Map<Integer, Double> tierMultipliers = new HashMap<>();

    private final Map<XMaterial, int[]> vectors = new HashMap<>();
    private final Map<XMaterial, Integer> materialTiers = new HashMap<>();

    public void load(Reader reader) {
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(reader, JsonObject.class);

        // single_only metadata
        if (root.has("_meta")) {
            JsonObject meta = root.getAsJsonObject("_meta");
            if (meta.has("single_only")) {
                Type listType = new TypeToken<List<String>>() { }.getType();
                singleOnlyTags.addAll(gson.fromJson(meta.get("single_only"), listType));
            }
        }

        // Item data
        Type itemsType = new TypeToken<Map<String, JsonElement>>() { }.getType();
        Map<String, JsonElement> rawItems = gson.fromJson(root.get("items"), itemsType);

        Map<XMaterial, List<String>> parsedItems = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : rawItems.entrySet()) {
            Optional<XMaterial> xMat = XMaterial.matchXMaterial(entry.getKey());
            if (xMat.isPresent() && !bannedItems.contains(xMat.get())) {
                XMaterial mat = xMat.get();
                JsonElement el = entry.getValue();

                int tier = defaultTier;
                List<String> tags = new ArrayList<>();

                if (el.isJsonArray()) { // no-tier item
                    tags = gson.fromJson(el, new TypeToken<List<String>>() { }.getType());
                } else if (el.isJsonObject()) { // tier item
                    JsonObject obj = el.getAsJsonObject();
                    if (obj.has("tier")) {
                        tier = obj.get("tier").getAsInt();
                    }
                    if (obj.has("tags")) {
                        tags = gson.fromJson(obj.get("tags"), new TypeToken<List<String>>() { }.getType());
                    }
                }

                parsedItems.put(mat, tags);
                materialTiers.put(mat, tier);
                allUniqueTags.addAll(tags);
            }
        }

        List<String> tagsIndex = new ArrayList<>(allUniqueTags);

        for (Map.Entry<XMaterial, List<String>> entry : parsedItems.entrySet()) {
            int[] vec = new int[tagsIndex.size()];
            for (int i = 0; i < tagsIndex.size(); i++) {
                if (entry.getValue().contains(tagsIndex.get(i))) {
                    vec[i] = 1;
                }
            }
            vectors.put(entry.getKey(), vec);
        }

        for (XMaterial coreMat : parsedItems.keySet()) {
            List<SynergyItem> synergies = calculateSynergiesFor(coreMat, parsedItems, maxSimilarity, topSimilar);
            int itemTier = materialTiers.getOrDefault(coreMat, defaultTier);

            CoreItem coreItem = new CoreItem(coreMat, itemTier, synergies, SynergyMode.STRICT);
            cache.put(coreMat, coreItem);

            int weight = calculateCoreWeight(parsedItems.get(coreMat), itemTier);
            if (weight > 0) {
                totalCoreWeight += weight;
                weightedCores.put(totalCoreWeight, coreMat);
            }
        }
    }

    public void loadBanned(Reader reader) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() { }.getType();
        List<String> bannedList = gson.fromJson(reader, listType);

        if (bannedList != null) {
            for (String patternStr : bannedList) {
                if (patternStr.contains("*")) {
                    String regex = "^" + patternStr.replace("*", ".*") + "$";
                    Pattern pattern = Pattern.compile(regex);

                    for (XMaterial mat : XMaterial.values()) {
                        if (pattern.matcher(mat.name()).matches()) {
                            bannedItems.add(mat);
                        }
                    }
                } else {
                    Optional<XMaterial> mat = XMaterial.matchXMaterial(patternStr);
                    mat.ifPresent(bannedItems::add);
                }
            }
        }
    }

    public void loadBannedEntities(Reader reader) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() { }.getType();
        List<String> bannedList = gson.fromJson(reader, listType);

        if (bannedList != null) {
            for (String typeStr : bannedList) {
                try {
                    bannedEntities.add(EntityType.valueOf(typeStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Unknown entity type in banned_entities: " + typeStr);
                }
            }
        }
    }

    public void loadConfig(Reader reader) {
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(reader, JsonObject.class);

        if (root.has("settings")) {
            JsonObject settings = root.getAsJsonObject("settings");
            if (settings.has("default_base_weight")) {
                defaultBaseWeight = settings.get("default_base_weight").getAsInt();
            }
            if (settings.has("default_tier")) {
                defaultTier = settings.get("default_tier").getAsInt();
            }
            if (settings.has("entity_spawn_chance")) {
                entitySpawnChance = settings.get("entity_spawn_chance").getAsDouble();
            }
            if (settings.has("entity_drop_weight")) {
                entityDropWeight = settings.get("entity_drop_weight").getAsDouble();
            }
            if (settings.has("special_spawn_chance")) {
                specialSpawnChance = settings.get("special_spawn_chance").getAsDouble();
            }
            if (settings.has("special_drop_weight")) {
                specialDropWeight = settings.get("special_drop_weight").getAsDouble();
            }
            if (settings.has("max_similarity")) {
                maxSimilarity = settings.get("max_similarity").getAsDouble();
            }
            if (settings.has("top_similar")) {
                topSimilar = settings.get("top_similar").getAsInt();
            }
            if (settings.has("semistrict_mode_weight")) {
                semistrictModeWeight = settings.get("semistrict_mode_weight").getAsDouble();
            }
            if (settings.has("v2_mode_weight")) {
                v2ModeWeight = settings.get("v2_mode_weight").getAsDouble();
            }
        }

        if (root.has("mode")) {
            String modeStr = root.get("mode").getAsString().toUpperCase();
            try {
                this.defaultMode = SynergyMode.valueOf(modeStr);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Unknown synergy mode in config: " + modeStr + ". Using STRICT.");
            }
        }

        if (root.has("tag_modifiers")) {
            JsonObject modifiers = root.getAsJsonObject("tag_modifiers");
            for (Map.Entry<String, JsonElement> entry : modifiers.entrySet()) {
                tagModifiers.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }

        if (root.has("tier_multipliers")) {
            JsonObject multipliers = root.getAsJsonObject("tier_multipliers");
            for (Map.Entry<String, JsonElement> entry : multipliers.entrySet()) {
                tierMultipliers.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsDouble());
            }
        }
    }

    private List<SynergyItem> calculateSynergiesFor(XMaterial anchor, Map<XMaterial,
            List<String>> allItems, double maxSimilarity, int topSimilar) {

        int[] anchorVec = vectors.get(anchor);
        List<String> anchorTags = allItems.get(anchor);
        List<SynergyItem> rawCandidates = new ArrayList<>();

        for (Map.Entry<XMaterial, List<String>> candidate : allItems.entrySet()) {
            XMaterial candMat = candidate.getKey();
            if (candMat == anchor) {
                continue;
            }

            int[] candVec = vectors.get(candMat);
            float sim = cosineSimilarity(anchorVec, candVec);

            if (sim > 0 && sim < maxSimilarity) {
                int candTier = defaultTier;
                int candWeight = calculateCoreWeight(candidate.getValue(), candTier);

                float weightMultiplier = (float) candWeight / defaultBaseWeight;
                float finalChance = (sim * 100) * weightMultiplier;
                finalChance = Math.min(finalChance, 100.0f);

                rawCandidates.add(new SynergyItem(candMat, finalChance));
            }
        }

        rawCandidates.sort((a, b) -> Float.compare(b.getSynweight(), a.getSynweight()));

        List<SynergyItem> result = new ArrayList<>();
        Set<String> activeExclusives = new HashSet<>();

        for (String tag : anchorTags) {
            if (singleOnlyTags.contains(tag)) {
                activeExclusives.add(tag);
            }
        }

        for (SynergyItem cand : rawCandidates) {
            List<String> candTags = allItems.get(cand.getMaterial());
            boolean conflict = false;

            for (String tag : candTags) {
                if (activeExclusives.contains(tag)) {
                    conflict = true;
                    break;
                }
            }

            if (!conflict) {
                result.add(cand);
                for (String tag : candTags) {
                    if (singleOnlyTags.contains(tag)) {
                        activeExclusives.add(tag);
                    }
                }
                if (result.size() >= topSimilar) {
                    break;
                }
            }
        }

        return result;
    }

    private int calculateCoreWeight(List<String> tags, int tier) {
        int weight = defaultBaseWeight;

        for (String tag : tags) {
            weight += tagModifiers.getOrDefault(tag, 0);
        }

        double multiplier = tierMultipliers.getOrDefault(tier, 1.0);

        return (int) Math.max(1, weight * multiplier);
    }

    private float cosineSimilarity(int[] vec1, int[] vec2) {
        int dotProduct = 0, mag1 = 0, mag2 = 0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            mag1 += vec1[i] * vec1[i];
            mag2 += vec2[i] * vec2[i];
        }
        if (mag1 == 0 || mag2 == 0) {
            return 0;
        }
        return (float) (dotProduct / (Math.sqrt(mag1) * Math.sqrt(mag2)));
    }

    public CoreItem get(XMaterial material) {
        return cache.get(material);
    }

    public Set<XMaterial> getLoadedMaterials() {
        return cache.keySet();
    }

    public XMaterial getRandomCore() {
        if (weightedCores.isEmpty()) {
            return XMaterial.CARROT;
        }
        int roll = ThreadLocalRandom.current().nextInt(totalCoreWeight) + 1;
        return weightedCores.ceilingEntry(roll).getValue();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public int getTier(XMaterial material) {
        return materialTiers.getOrDefault(material, defaultTier);
    }
}
