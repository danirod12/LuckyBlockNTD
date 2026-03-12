package com.github.danirod12.luckyblock.util;

import java.util.ArrayList;
import java.util.List;

public class StringMatcher<T> extends ArrayList<String> {

    private final Type type;
    private final T data;

    public StringMatcher(T data, String type, List<String> list) {
        this(data, Type.parse(type), list);
    }

    public StringMatcher(T data, Type type, List<String> list) {
        this.data = data;
        this.type = type;
        this.addAll(list);
    }

    public StringMatcher(T data, Type type) {
        this.data = data;
        this.type = type;
    }

    public StringMatcher(T data, String mode) {
        this(data, Type.parse(mode));
    }

    public Type getType() {
        return type;
    }

    public boolean isEnabled(String element) {
        if (element == null) {
            return true;
        }
        switch (type) {
            case WHITELIST:
                return containsIgnoreCase(element);
            case BLACKLIST:
                return !containsIgnoreCase(element);
            default:
                return true;
        }
    }

    public T getDataHandler() {
        return data;
    }

    private boolean containsIgnoreCase(String element) {
        for (String string : this) {
            if (element.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    public enum Type {

        DISABLED("disabled"), WHITELIST("whitelist"), BLACKLIST("blacklist");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static Type parse(String name) {
            for (Type type : values()) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return DISABLED;
        }

        public String getName() {
            return name;
        }
    }
}
