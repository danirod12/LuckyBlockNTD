package me.DenBeKKer.ntdLuckyBlock.api;

import me.DenBeKKer.ntdLuckyBlock.api.exception.ApiNotInitializedException;
import me.DenBeKKer.ntdLuckyBlock.api.exception.StaticMethodsOnlyException;
import me.DenBeKKer.ntdLuckyBlock.api.model.Identifier;
import me.DenBeKKer.ntdLuckyBlock.api.model.PluginVersion;
import me.DenBeKKer.ntdLuckyBlock.api.provider.GenerationFactoryProvider;
import me.DenBeKKer.ntdLuckyBlock.api.provider.LBMainProvider;
import me.DenBeKKer.ntdLuckyBlock.api.provider.LuckyEngineProvider;
import me.DenBeKKer.ntdLuckyBlock.api.provider.LuckyRecipeProvider;
import me.DenBeKKer.ntdLuckyBlock.api.util.LogChannel;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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

    public static GenerationFactoryProvider getGenerationFactoryProvider() {
        if (luckyEngineProvider == null) {
            throw new ApiNotInitializedException();
        }
        return luckyEngineProvider.getGenerationFactory();
    }

    public static LogChannel getLogger() {
        return provider.getLogChannel();
    }

    /**
     * Apply custom tag to item
     * <p>
     * Note: This method does not modify the original ItemStack
     *
     * @param stack      ItemStack to apply tag
     * @param identifier Identifier to apply
     * @return ItemStack with applied tag
     */
    public static ItemStack insertTag(ItemStack stack, Identifier identifier) {
        return luckyEngineProvider.getVersionControl().apply(stack, identifier);
    }

    /**
     * Check if item has custom tag and value is equal to given
     *
     * @param stack      ItemStack to check
     * @param identifier Identifier to check
     * @param value      Value to check. Could be null if you want to check only tag presence
     * @return true if item has tag
     */
    public static boolean checkTag(ItemStack stack, Identifier identifier, @Nullable String value) {
        String id = getTagValue(stack, identifier);
        return id != null && (value == null || id.equals(value));
    }

    /**
     * Get tag value from item
     *
     * @param stack      ItemStack to get tag
     * @param identifier Identifier to get
     * @return Tag value or null if tag not found
     */
    public static String getTagValue(ItemStack stack, Identifier identifier) {
        return luckyEngineProvider.getVersionControl().getValue(stack, identifier);
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
