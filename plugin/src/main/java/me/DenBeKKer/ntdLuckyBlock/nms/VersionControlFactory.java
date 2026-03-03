package me.DenBeKKer.ntdLuckyBlock.nms;

import com.github.danirod12.mcversion.MinecraftVersion;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.DenBeKKer.ntdLuckyBlock.nms.material.IMat;
import me.DenBeKKer.ntdLuckyBlock.nms.material.Mat1_12;
import me.DenBeKKer.ntdLuckyBlock.nms.material.Mat1_13;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import me.DenBeKKer.ntdLuckyBlock.util.Templates;
import me.DenBeKKer.ntdLuckyBlock.util.manager.LogChannel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class VersionControlFactory {

    private final IMat materialFactory;
    private final Material tinted;
    private final ItemTag itemTagAdapter;

    public VersionControlFactory(LogChannel logChannel) {
        MinecraftVersion.NMSVersion nmsVersion = MinecraftVersion.getPossibleNMSVersion();
        logChannel.info("Loading NMS for your platform ("
                + Bukkit.getVersion() + ", " + nmsVersion.getCraftBukkitName() + ")...");
        // Load NMS classes
        switch (nmsVersion) {
            case v1_21_R1: {
                itemTagAdapter = new ItemTag1_21_R1();
                break;
            }
            case v1_20_R4: {
                itemTagAdapter = new ItemTag1_20_R4();
                break;
            }
            case v1_20_R3: {
                itemTagAdapter = new ItemTag1_20_R3();
                break;
            }
            case v1_20_R2: {
                itemTagAdapter = new ItemTag1_20_R2();
                break;
            }
            case v1_20_R1: {
                itemTagAdapter = new ItemTag1_20_R1();
                break;
            }
            case v1_19_R3: {
                itemTagAdapter = new ItemTag1_19_R3();
                break;
            }
            case v1_19_R2: {
                itemTagAdapter = new ItemTag1_19_R2();
                break;
            }
            case v1_19_R1: {
                itemTagAdapter = new ItemTag1_19_R1();
                break;
            }
            case v1_18_R2: {
                itemTagAdapter = new ItemTag1_18_R2();
                break;
            }
            case v1_18_R1: {
                itemTagAdapter = new ItemTag1_18_R1();
                break;
            }
            default: {
                try {
                    itemTagAdapter = new ItemTagLegacy();
                    break;
                } catch (UnsupportedOperationException ex) {
                    throw new RuntimeException("Platform is not supported");
                }
            }
        }

        // Load material adapters
        // Material
        materialFactory = Misc.getEnum(Material.class, "PLAYER_HEAD") == null ? new Mat1_12() : new Mat1_13();
        logChannel.info("Loaded " + materialFactory.build() + " material version");

        Material tinted = Misc.getEnum(Material.class, "TINTED_GLASS");
        this.tinted = tinted == null || !Templates.VERSION.isPremium() ? null : tinted;
    }

    public String getNmsVersion() {
        return MinecraftVersion.getPossibleNMSVersion().getCraftBukkitName();
    }

    public IMat getMat() {
        return materialFactory;
    }

    public boolean isModern() {
        return materialFactory instanceof Mat1_13;
    }

    public boolean isLegacy() {
        return materialFactory instanceof Mat1_12;
    }

    public Material getTintedMaterial() {
        return tinted;
    }

    public ItemTag getItemTagAdapter() {
        return itemTagAdapter;
    }

    public ItemStack createPlayerHead(String url, String name, List<String> lore, UUID uuid) {
        if (url == null || url.isEmpty())
            throw new IllegalArgumentException("URL cannot be null");
        if (!url.contains("textures.minecraft.net/texture")) {
            url = "http://textures.minecraft.net/texture/" + url;
        }

        ItemStack head = this.materialFactory.getItem(IMat.Mat.PLAYER_SKULL, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;

        GameProfile profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid, null);
        profile.getProperties().put("textures", new Property("textures",
                new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()))));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        headMeta.setDisplayName(name);
        headMeta.setLore(lore);

        head.setItemMeta(headMeta);
        return head;
    }
}
