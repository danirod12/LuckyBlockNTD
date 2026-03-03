package me.DenBeKKer.ntdLuckyBlock.util;

import me.DenBeKKer.ntdLuckyBlock.api.model.PluginVersion;

public class Templates {

    public static final PluginVersion VERSION = PluginVersion.FREE;
    public static final String COMPILE_VERSION = "${project.version}";

    public static final String COMPILE_DATE = "${timestamp}";
    public static final int BUILD = 132;

    public static final String DISCORD_URL = "https://discord.gg/vbYW3sperj";
    public static final String API_USAGE_URL = "https://clck.ru/34gZSd https://shorturl.at/gNOUX";
    public static final String LUCKYBLOCK_SETUP_URL = "https://clck.ru/34gZSS https://shorturl.at/lATX6";
    public static final String BSTATS_URL = "https://clck.ru/34NnhT https://shorturl.at/akrJM";

    public static final String[] SUPPORT_INFO;

    static {
        SUPPORT_INFO = new String[]{
                "§f=-= §6SUPPORT & BUG REPORTING & FEATURE REQUESTING §f=-=",
                "§8 > §bDiscord §f- " + DISCORD_URL,
                "§8 > §9Vkontakte §f- https://vk.com/danirodplay §7(Rus)",
                "§8 > §6API usage §f- " + API_USAGE_URL,
                "§8 > §6LuckyBlock setup §f- " + LUCKYBLOCK_SETUP_URL,
                "§f=-= §6SUPPORT & BUG REPORTING & FEATURE REQUESTING §f=-="
        };
    }
}
