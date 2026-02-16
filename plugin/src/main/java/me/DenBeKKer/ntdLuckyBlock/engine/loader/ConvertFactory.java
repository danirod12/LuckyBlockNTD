package me.DenBeKKer.ntdLuckyBlock.engine.loader;

import me.DenBeKKer.ntdLuckyBlock.util.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
