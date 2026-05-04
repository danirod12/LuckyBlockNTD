package com.github.danirod12.luckyblock.engine.generator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class AdvancedLootDatabase {
    private int defaultBaseWeight = 10;
    private int defaultTier = 1;

    private final Map<Material, CoreItem> cache = new HashMap<>();

    private final NavigableMap<Integer, Material> weightedCores = new TreeMap<>();
    private int totalCoreWeight = 0;

    private final List<String> singleOnlyTags = new ArrayList<>();
    private final Set<String> allUniqueTags = new HashSet<>();
    private final Set<Material> bannedItems = new HashSet<>();
    private final Map<String, Integer> tagModifiers = new HashMap<>();
    private final Map<Integer, Double> tierMultipliers = new HashMap<>();

    private final Map<Material, int[]> vectors = new HashMap<>();

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
        Type itemsType = new TypeToken<Map<String, List<String>>>() { }.getType();
        Map<String, List<String>> rawItems = gson.fromJson(root.get("items"), itemsType);

        Map<Material, List<String>> parsedItems = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : rawItems.entrySet()) {
            Material mat = Material.matchMaterial(entry.getKey());
            if (mat != null && !bannedItems.contains(mat)) {
                parsedItems.put(mat, entry.getValue());
                allUniqueTags.addAll(entry.getValue());
            }
        }

        List<String> tagsIndex = new ArrayList<>(allUniqueTags);

        // Vectors for cos_sim
        for (Map.Entry<Material, List<String>> entry : parsedItems.entrySet()) {
            int[] vec = new int[tagsIndex.size()];
            for (int i = 0; i < tagsIndex.size(); i++) {
                if (entry.getValue().contains(tagsIndex.get(i))) {
                    vec[i] = 1;
                }
            }
            vectors.put(entry.getKey(), vec);
        }

        for (Material coreMat : parsedItems.keySet()) {
            List<SynergyItem> synergies = calculateSynergiesFor(coreMat, parsedItems, 0.7f);

            int itemTier = defaultTier; //TODO(zhabka_zhaba): Proper JSON tiers + parsing

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

                    for (Material mat : Material.values()) {
                        if (pattern.matcher(mat.name()).matches()) {
                            bannedItems.add(mat);
                        }
                    }
                } else {
                    Material mat = Material.matchMaterial(patternStr);
                    if (mat != null) {
                        bannedItems.add(mat);
                    }
                }
            }
        }
    }

    public void loadWeights(Reader reader) {
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(reader, JsonObject.class);

        if (root.has("settings")) {
            JsonObject settings = root.getAsJsonObject("settings");
            defaultBaseWeight = settings.get("default_base_weight").getAsInt();
            defaultTier = settings.get("default_tier").getAsInt();
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

    private List<SynergyItem> calculateSynergiesFor(Material anchor, Map<Material,
            List<String>> allItems, float maxSim) {
        List<SynergyItem> result = new ArrayList<>();
        int[] anchorVec = vectors.get(anchor);
        List<String> anchorTags = allItems.get(anchor);

        // single_only tags search
        boolean hasExclusive = anchorTags.stream().anyMatch(singleOnlyTags::contains);
        List<String> anchorExclusives = new ArrayList<>();
        if (hasExclusive) {
            for (String tag : anchorTags) {
                if (singleOnlyTags.contains(tag)) {
                    anchorExclusives.add(tag);
                }
            }
        }

        for (Map.Entry<Material, List<String>> candidate : allItems.entrySet()) {
            Material candMat = candidate.getKey();
            if (candMat == anchor) {
                continue;
            }

            if (hasExclusive) {
                boolean conflict = candidate.getValue().stream().anyMatch(anchorExclusives::contains);
                if (conflict) {
                    continue;
                }
            }

            int[] candVec = vectors.get(candMat);
            float sim = cosineSimilarity(anchorVec, candVec);

            if (sim > 0 && sim < maxSim) {
                result.add(new SynergyItem(candMat, sim * 100));
            }
        }

        result.sort((a, b) -> Float.compare(b.getSynweight(), a.getSynweight()));

        return result.size() > 10 ? result.subList(0, 10) : result;
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

    public CoreItem get(Material material) {
        return cache.get(material);
    }

    public Material getRandomCore() {
        if (weightedCores.isEmpty()) {
            return Material.CARROT;
        }
        int roll = ThreadLocalRandom.current().nextInt(totalCoreWeight) + 1;
        return weightedCores.ceilingEntry(roll).getValue();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
