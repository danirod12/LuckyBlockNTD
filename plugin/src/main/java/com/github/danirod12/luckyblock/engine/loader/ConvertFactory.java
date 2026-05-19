package com.github.danirod12.luckyblock.engine.loader;

import com.github.danirod12.luckyblock.api.util.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated // V2f -> V2p conversion, not used anymore
public class ConvertFactory {

    private final Map<Config, List<String>> map = new HashMap<>();

    public void add(Config loaded, String path) {
        map.computeIfAbsent(loaded, n -> new ArrayList<>()).add(path);
    }

    public int getRequests() {
        return map.values().stream().mapToInt(List::size).sum();
    }

    public Map<Config, List<String>> getRequestMap() {
        return new HashMap<>(map);
    }
}
