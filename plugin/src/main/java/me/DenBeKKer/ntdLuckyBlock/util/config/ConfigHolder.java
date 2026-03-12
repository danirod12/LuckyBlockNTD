package me.DenBeKKer.ntdLuckyBlock.util.config;

import lombok.Getter;
import lombok.Setter;
import me.DenBeKKer.ntdLuckyBlock.api.util.Config;
import me.DenBeKKer.ntdLuckyBlock.api.util.Single;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ConfigHolder {

    @Setter
    @Getter
    private Config config;

    @ConfigField(path = "inform-about-update")
    public boolean informAboutUpdates;

    @ConfigField(path = "web-unavailable-disable")
    public boolean webUnavailableDisable;

    @ConfigField(path = "force-update-inventory")
    public boolean forceUpdateInventory;

    @ConfigField(path = "disable-json-convert-checking")
    public boolean disableJsonConvertCheck;

    @ConfigField(path = "break-permissions")
    public boolean breakPermissions;

    @ConfigField(path = "permission-for-each-give-get")
    public boolean getCommandPermissionsForEach;

    @ConfigField(path = "permission-for-each-gui-get")
    public boolean getShopPermissionsForEach;

    @ConfigField(path = "craft-permissions")
    public boolean craftPermissions;

    @ConfigField(path = "craft-dye-permissions")
    public boolean craftDyePermissions;

    @ConfigField(path = "reduce-command-author-info")
    public boolean reduceAuthorInfo;

    @ConfigField(path = "prevent-hat-luckyblocks")
    public boolean preventHatLuckyBlock;

    @ConfigField(path = "light-source")
    public boolean lightSource;

    @ConfigField(path = "worldedit-mask")
    public List<String> worldEditMask;

    @ConfigField(path = "event-priority.block-break-event")
    public boolean breakEventHighestPriority;

    public void dirtyPickUp() {
        if (this.config == null) {
            throw new RuntimeException();
        }
        for (Field field : this.getClass().getFields()) {
            if (field.isAnnotationPresent(ConfigField.class)) {
                try {
                    String path = field.getAnnotation(ConfigField.class).path();
                    if (field.getType() == boolean.class) {
                        field.set(this, config.getBoolean(path));
                    } else if (field.getType() == Single.class) {
                        if (field.getGenericType() == boolean.class) {
                            Single.class.getMethod("set", Object.class).invoke(field, config.get().get(path));
                        }
                    } else if (field.getType() == List.class && field.getGenericType() instanceof ParameterizedType) {
                        Type[] generics = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                        if (generics.length == 1 && generics[0] == String.class) {
                            field.set(this, config.getStringList(path));
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
