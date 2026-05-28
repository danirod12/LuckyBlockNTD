package com.github.danirod12.luckyblock.api;

import com.github.danirod12.luckyblock.api.exception.ApiNotInitializedException;
import com.github.danirod12.luckyblock.api.exception.StaticMethodsOnlyException;
import com.github.danirod12.luckyblock.api.model.PluginVersion;
import com.github.danirod12.luckyblock.api.provider.LBMainProvider;
import com.github.danirod12.luckyblock.api.provider.LuckyEngineProvider;
import com.github.danirod12.luckyblock.api.provider.LuckyRecipeProvider;
import com.github.danirod12.luckyblock.api.util.LogChannel;

public class LuckyBlockAPI {

    public LuckyBlockAPI() {
        throw new StaticMethodsOnlyException();
    }

    private static LBMainProvider provider;
    private static LuckyEngineProvider luckyEngineProvider;

    public static LBMainProvider getInstance() {
        if (provider == null) {
            throw new ApiNotInitializedException();
        }
        return provider;
    }

    public static LuckyEngineProvider getLuckyEngineProvider() {
        if (luckyEngineProvider == null) {
            throw new ApiNotInitializedException();
        }
        return luckyEngineProvider;
    }

    public static LuckyRecipeProvider getLuckyRecipeProvider() {
        if (luckyEngineProvider == null) {
            throw new ApiNotInitializedException();
        }
        return luckyEngineProvider.getRecipeProvider();
    }

    public static LogChannel getLogger() {
        return provider.getLogChannel();
    }

    public static void injectAPI(LBMainProvider provider, LuckyEngineProvider engine) {
        LuckyBlockAPI.provider = provider;
        luckyEngineProvider = engine;
    }

    public static void reloadConfig() {
        provider.reloadConfig();
    }

    public static void reloadSystem() {
        provider.reloadSystem();
    }

    public static String getVersion() {
        return provider.getVersion();
    }

    public static String getLastUpdate() {
        return provider.getLastUpdate();
    }

    public static PluginVersion getVersionType() {
        return provider.getVersionType();
    }
}
