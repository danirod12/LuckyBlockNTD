package me.DenBeKKer.ntdLuckyBlock.variables;

import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.provider.VersionControl;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayerHead {

    PLUS_WOOD("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"),
    PLUS_STONE("0a21eb4c57750729a48b88e9bbdb987eb6250a5bc2157b59316f5f1887db5"),
    MINUS_WOOD("bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193"),
    MINUS_STONE("a8c67fed7a2472b7e9afd8d772c13db7b82c32ceeff8db977474c11e4611"),

    NEXT_WOOD("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"),
    PREVIOUS_WOOD("b6e14f2b7d1f5cb6f56ab3e6881fc8490eda6ff2d1d48a3241d147223cb3"),
    CLOSE_WOOD("5b30507783c37db3a3092cad043e57951aa8b4c6ea9acc47d604b7eb5aea028");

    private final String url;
    private ItemStack stack;

    PlayerHead(String url) {
        this.url = "http://textures.minecraft.net/texture/" + url;
    }

    public static void loadAll(VersionControl versionControl) {
        for (PlayerHead value : PlayerHead.values()) {
            value.loadHead(versionControl);
        }
    }

    public void loadHead(VersionControl versionControl) {
        stack = versionControl.getPlayerHead(url, "name", new ArrayList<>(), null);
    }

    public String getURL() {
        return url;
    }

    public ItemStack getHead(String name, List<String> lore) {
        if (stack == null) {
            loadHead(LuckyBlockAPI.getLuckyEngineProvider().getVersionControl());
        }
        ItemStack item = stack.clone();

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name == null ? null : Misc.setColors(name));
        meta.setLore(lore == null ? null : lore.stream().map(Misc::setColors).collect(Collectors.toList()));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack getHead() {
        return this.getHead(null, null);
    }
}
