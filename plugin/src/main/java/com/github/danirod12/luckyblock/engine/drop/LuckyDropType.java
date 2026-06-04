package com.github.danirod12.luckyblock.engine.drop;

import com.github.danirod12.luckyblock.api.model.LuckyDrop;
import com.github.danirod12.luckyblock.engine.loader.SpecialDropLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LuckyDropType {
    LUCKY_BLOCK_ITEM(LuckyItemDrop.class),
    ITEM(ItemDrop.class),
    SPECIAL(SpecialDropLoader.class),
    ENTITY(EntityDrop.class),
    COMMAND(CommandDrop.class),
    CONSOLE(ConsoleDrop.class),
    OPPED(OppedDrop.class),
    MESSAGE(MessageDrop.class),
    SCHEMATIC(SchematicDrop.class),
//    @Deprecated
//    CUSTOM_ITEM, TODO add backwards compatibility via V2 -> V3

    ;

    @Getter
    private final Class<? extends LuckyDrop> clazz;

    public static LuckyDropType fromString(String type) {
        for (LuckyDropType dropType : values()) {
            if (dropType.name().equalsIgnoreCase(type)) {
                return dropType;
            }
        }
        return null;
    }
}
