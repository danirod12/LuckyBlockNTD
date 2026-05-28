package com.github.danirod12.luckyblock.api.model;

import com.github.danirod12.luckyblock.api.util.ColorData;
import lombok.Getter;

/**
 * Represents the type of a lucky block, which determines its color and appearance.
 */
public enum LuckyBlockType {

    BLACK(ColorData.BLACK),
    BLUE(ColorData.BLUE),
    BROWN(ColorData.BROWN),
    CYAN(ColorData.CYAN),
    GRAY(ColorData.GRAY),
    GREEN(ColorData.GREEN),
    LIGHT_BLUE(ColorData.LIGHT_BLUE),
    LIGHT_GRAY(ColorData.LIGHT_GRAY),
    LIME(ColorData.LIME),
    MAGENTA(ColorData.MAGENTA),
    ORANGE(ColorData.ORANGE),
    PINK(ColorData.PINK),
    PURPLE(ColorData.PURPLE),
    RED(ColorData.RED),
    WHITE(ColorData.WHITE),
    YELLOW(ColorData.YELLOW),

    TINTED(ColorData.BLACK),
    ICED(ColorData.CYAN);

    @Getter
    private final ColorData colorData;

    LuckyBlockType(ColorData colorData) {
        this.colorData = colorData;
    }

    public static LuckyBlockType parse(String name) {
        for (LuckyBlockType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
