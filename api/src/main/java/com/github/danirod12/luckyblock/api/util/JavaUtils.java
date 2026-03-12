package com.github.danirod12.luckyblock.api.util;

public class JavaUtils {
    public static Class<?> getClass(String path) {
        try {
            return Class.forName(path);
        } catch (Throwable th) {
            return null;
        }
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (Exception exception) {
            return null;
        }
    }
}
