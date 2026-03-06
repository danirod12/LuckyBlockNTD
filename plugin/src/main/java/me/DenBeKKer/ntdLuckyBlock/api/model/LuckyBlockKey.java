package me.DenBeKKer.ntdLuckyBlock.api.model;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockAPI;
import me.DenBeKKer.ntdLuckyBlock.api.util.ColorData;
import org.bukkit.Material;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Getter
public class LuckyBlockKey {

    private static final Pattern KEY_PATTERN = Pattern.compile("^(?!.*__)([a-zA-Z][a-zA-Z_]{1,30}[a-zA-Z])$");
    private final String key;
    private final LuckyBlockType internal;
    private final ColorData colorData;
    private final Material material;
    private final boolean applyColorDataToBlock;

    public LuckyBlockKey(String key, ColorData colorData, Material material, boolean applyColorDataToBlock) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(colorData);
        Objects.requireNonNull(material);
        if (key.equalsIgnoreCase("random")) {
            throw new IllegalArgumentException("Key cannot be 'random'");
        }
        if (!KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("Incorrect key. Key could contain a-z letters and _, but cannot"
                    + " start or end with _. Key length should be from 3 to 32 chars. You cannot use __");
        }
        if (!material.isSolid()) {
            throw new IllegalArgumentException("Provided material is not solid");
        }
        this.key = key.toLowerCase();
        this.internal = LuckyBlockType.parse(key);
        this.colorData = this instanceof NotLoadedLuckyBlockKey
                && this.internal != null ? this.internal.getColorData() : colorData;
        this.material = material;
        this.applyColorDataToBlock = applyColorDataToBlock;
    }

    public String getDefaultCustomName() {
        return '§' + colorData.asColorCode() + String.valueOf(key.toCharArray()[0]).toUpperCase()
                + key.substring(1).replace("_", "") + " LuckyBlock";
    }

    public boolean isInternal() {
        return internal != null;
    }

    public Optional<LuckyBlock> getSetup() {
        return LuckyBlockAPI.getLuckyEngineProvider().get(this);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LuckyBlockKey) {
            return ((LuckyBlockKey) obj).key.equals(key);
        }
        return false;
    }

    @Override
    public String toString() {
        return key;
    }

    public static class NotLoadedLuckyBlockKey extends LuckyBlockKey {

        public NotLoadedLuckyBlockKey(String key) {
            super(key, ColorData.WHITE, Material.GLASS, false);
        }
    }
}
