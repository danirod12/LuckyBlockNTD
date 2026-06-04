package com.github.danirod12.luckyblock.engine.generator;

import com.cryptomorin.xseries.XMaterial;
import com.github.danirod12.luckyblock.api.exception.StaticMethodsOnlyException;
import com.github.danirod12.luckyblock.api.model.LuckyBlockKey;
import com.github.danirod12.luckyblock.api.model.LuckyBlockType;
import com.github.danirod12.luckyblock.api.util.ColorData;
import org.bukkit.Material;

import java.util.UUID;

public class BaseDataGenerator {

    public BaseDataGenerator() {
        throw new StaticMethodsOnlyException();
    }

    public static String getTexture(LuckyBlockKey type) {
        switch (type.isInternal() ? type.getInternal() : LuckyBlockType.YELLOW) {
            case TINTED:
                return "a57a8c5ef6cafefa50eb308b97a6375b132def6fb6c50b107fbb39a28fa6c227";
            case ICED:
                return "2fbe5d2c82ef397513cd757f7735e653238fb62de672907e6b4762a1767afa7d";
            case BLACK:
                return "c7b187e38b407feabaf190879d98a67f4c7052f3201f72e44571f537ea89d4c7";
            case BLUE:
                return "8534b17d2d3b5a64c57f5f080dd777945761d9f71d82e8f599f242976e4d0c05";
            case BROWN:
                return "c133414759f9e5315156017023a8b1b31cfbf177dcafb0603b8e9d94036a23f1";
            case CYAN:
                return "300c817432585fadcbd9ca8cfedfeedbce5851b7c07d1b073fb52fa8283e8f23";
            case GRAY:
                return "f171a0c0572824da80366d71c5413a7e3355abec79b8f529f1dc9dad2632f485";
            case GREEN:
                return "6ab180e820f71629f01f25b62bdeb9c324d50a38af74e6a9321439471046dc41";
            case LIGHT_BLUE:
                return "65fed46b39c6c34d491143b72ce728797d16f9b5aa40bbdec61667f5ff9d3d45";
            case LIGHT_GRAY:
                return "d019fe633a45fc3d353f9a2f09fd88e721de31c2bc969ae0aeeadf57c1be0bcc";
            case LIME:
                return "b03f5a9bc719cd1127be0954c73cd5714ecd478dad880ef2b9979c481097e6be";
            case MAGENTA:
                return "1f23685c697ba55edda425ecf1fec72feeb7bb985c6506b2cef070515e4e5492";
            case ORANGE:
                return "bd3a1436e0fd5f3e4d113a092c1d07a12e22d426c50183ccd62877d9cd885fbd";
            case PINK:
                return "11a367164bc6852c8f078811de7baacaefd6964960ebff1fa504c7b4b1298a52";
            case PURPLE:
                return "6cfb4ebc3ba4fec04d8978a1660bb7c98e96342ff2a238ada9df2f74bf032b40";
            case RED:
                return "29942dd1338abeae8b8274a41ae1dcdf2b7be449f28d6b650ec06e491e70f570";
            case WHITE:
                return "c8e16e7ff13d3e30a3a2b835a86fd05ecbc08909f6056de1646fe25b3def9584";
            case YELLOW:
                return "17bc1b64cba3dc4cefe4e121c3cdbbb0fa99aba0e113b5c916815fc9b304e636";
            default:
                throw new UnsupportedOperationException("Not implemented texture for " + type);
        }
    }

    public static UUID genUUID(LuckyBlockKey type) {
        StringBuilder hash = new StringBuilder();
        String symbol;
        if (type.isInternal()) {
            LuckyBlockType internal = type.getInternal();
            if (internal.ordinal() > 15) {
                switch (internal) {
                    case TINTED:
                        hash.append("10");
                        break;
                    case ICED:
                        hash.append("11");
                        break;
                    default:
                        throw new UnsupportedOperationException("Not implemented UUID for " + internal);
                }
            } else {
                hash.append(Integer.toHexString(type.getInternal().getColorData().getData()));
            }
            symbol = "0";
        } else {
            int hashCode = type.hashCode();
            symbol = hashCode < 0 ? "1" : "2";
            hash.append(Integer.toHexString(hashCode < 0 ? -hashCode : hashCode));
        }

        while (hash.length() < 11) {
            hash.insert(0, "0");
        }

        return UUID.fromString("12345678-1234-1234-1234-" + symbol + hash); // V2 UUID format
    }

    public static boolean hasDefaultCrafts(LuckyBlockKey key) {
        if (key.isInternal()) {
            LuckyBlockType type = key.getInternal();
            return type != LuckyBlockType.ICED && type != LuckyBlockType.TINTED;
        }
        return false;
    }

    public static LuckyBlockKey getKey(LuckyBlockType type) {
        if (type == LuckyBlockType.ICED) {
            return new LuckyBlockKey("iced", ColorData.LIGHT_BLUE, Material.ICE, false);
        } else if (type == LuckyBlockType.TINTED) {
            Material tinted = XMaterial.TINTED_GLASS.get();
            if (tinted == null) {
                tinted = XMaterial.BLACK_STAINED_GLASS.get();
            }
            return new LuckyBlockKey("black", ColorData.BLACK, tinted, true);
        } else {
            ColorData data = type.getColorData();
            Material material = XMaterial.valueOf(data.name() + "_STAINED_GLASS").get();
            return new LuckyBlockKey(type.name(), data, material, true);
        }
    }
}
