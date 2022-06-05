package me.DenBeKKer.ntdLuckyBlock.util;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import me.DenBeKKer.ntdLuckyBlock.util.material.Mat1_12;
import org.bukkit.DyeColor;

public enum ColorData {

    WHITE((byte) 0),
    ORANGE((byte) 1),
    MAGENTA((byte) 2),
    LIGHT_BLUE((byte) 3),
    YELLOW((byte) 4),
    LIME((byte) 5),
    PINK((byte) 6),
    GRAY((byte) 7),
    LIGHT_GRAY((byte) 8),
    CYAN((byte) 9),
    PURPLE((byte) 10),
    BLUE((byte) 11),
    BROWN((byte) 12),
    GREEN((byte) 13),
    RED((byte) 14),
    BLACK((byte) 15);

    private final byte data;

    ColorData(byte data) {
        this.data = data;
    }

    public static ColorData parse(String name) {
        for (ColorData value : ColorData.values()) {
            if (value.name().equalsIgnoreCase(name))
                return value;
        }
        return null;
    }

    public static ColorData parse(byte data) {
        for (ColorData value : ColorData.values()) {
            if (value.data == data)
                return value;
        }
        return null;
    }

    public byte getData() {
        return data;
    }

    public byte getDyeData() {
        return (byte) (15 - data);
    }

    public DyeColor asDyeColor() {
        return name().equalsIgnoreCase("LIGHT_GRAY") && LBMain.getInstance().factory instanceof Mat1_12
                ? DyeColor.valueOf("SILVER") : DyeColor.valueOf(name());
    }

    public String asColorCode() {
        switch (data) {
            case 0:
                return "f";
            case 1:
                return "6";
            case 2:
            case 6:
                return "d";
            case 3:
                return "b";
            case 4:
                return "e";
            case 5:
                return "a";
            case 7:
                return "8";
            case 8:
                return "7";
            case 9:
                return "3";
            case 10:
                return "5";
            case 11:
                return "1";
            case 13:
                return "2";
            case 14:
                return "4";
            case 15:
                return "0";
        }
        return "c";
    }

    @Deprecated
    public DyeColor asDyeColorByWool() {
        return DyeColor.getByWoolData(getData());
    }

    @Deprecated
    public DyeColor asDyeColorAsDyeData() {
        return DyeColor.getByDyeData((byte) getDyeData());
    }

}
