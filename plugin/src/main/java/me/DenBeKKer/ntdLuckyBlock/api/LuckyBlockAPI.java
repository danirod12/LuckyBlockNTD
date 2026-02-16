package me.DenBeKKer.ntdLuckyBlock.api;

import me.DenBeKKer.ntdLuckyBlock.api.exception.ApiNotInitializedException;
import me.DenBeKKer.ntdLuckyBlock.api.exception.StaticMethodsOnlyException;
import me.DenBeKKer.ntdLuckyBlock.api.model.PluginVersion;
import me.DenBeKKer.ntdLuckyBlock.api.provider.GenerationFactoryProvider;
import me.DenBeKKer.ntdLuckyBlock.api.provider.JPAdapter;
import me.DenBeKKer.ntdLuckyBlock.api.provider.LuckyEngineProvider;
import me.DenBeKKer.ntdLuckyBlock.api.provider.LuckyRecipeProvider;
import me.DenBeKKer.ntdLuckyBlock.customitem.Identifier;
import me.DenBeKKer.ntdLuckyBlock.util.Templates;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class LuckyBlockAPI {

    public LuckyBlockAPI() {
        throw new StaticMethodsOnlyException();
    }

    private static JPAdapter jpAdapter;
    private static LuckyEngineProvider luckyEngineProvider;

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

    public static GenerationFactoryProvider getGenerationFactoryProvider() {
        if (luckyEngineProvider == null) {
            throw new ApiNotInitializedException();
        }
        return luckyEngineProvider.getGenerationFactory();
    }

    /**
     * Inserts to stack identified NBT tag
     *
     * @param stack   ItemStack to mark
     * @param plugin  Plugin instance
     * @param tagName Tag key
     * @param value   Value to insert
     * @return Item copy with inserted tag
     */
    public static ItemStack insertTag(ItemStack stack, Plugin plugin, String tagName, String value) {
        return new Identifier(plugin, tagName, value).apply(stack);
    }

    /**
     * Check if stack has identified NBT tag
     *
     * @param stack   ItemStack to check
     * @param plugin  Plugin instance
     * @param tagName Tag key
     * @param value   Value to compare
     * @return True if item has identified key and value equals to requested
     */
    public static boolean checkTag(ItemStack stack, Plugin plugin, String tagName, String value) {
        return new Identifier(plugin, tagName, value).compare(stack);
    }

    public static void __injectAPI(JPAdapter adapter, LuckyEngineProvider provider) {
        jpAdapter = adapter;
        luckyEngineProvider = provider;
    }

    public static void checkForUpdates(boolean notify) {
        jpAdapter.checkForUpdates(notify);
    }

    public static void reloadConfig() {
        jpAdapter.reloadConfig();
    }

    public static void reloadSystem() {
        jpAdapter.reloadSystem();
    }

    public static String getVersion() {
        return jpAdapter.getVersion();
    }

    public static String getLastUpdate() {
        return Templates.COMPILE_DATE;
    }

    public static int getBuild() {
        return Templates.BUILD;
    }

    public static PluginVersion getVersionType() {
        return Templates.VERSION;
    }
}
