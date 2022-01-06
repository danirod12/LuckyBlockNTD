package me.DenBeKKer.ntdLuckyBlock.util;

import java.util.ArrayList;
import java.util.Collection;

public class StringMatcher<T> extends ArrayList<String> {

    private final Type type;
    private T data;

    public enum Type {

        DISABLED("disabled"), WHITELIST("whitelist"), BLACKLIST("blacklist");

        private final String name;

        Type(String name) { this.name = name; }

        public String getName() { return name; }

        public static Type parse(String name) {
            for(Type type : values())
                if(type.getName().equalsIgnoreCase(name)) return type;
            return DISABLED;
        }

    }

    public StringMatcher(String mode, Collection<String> list) {
        this(Type.parse(mode), list);
    }

    public StringMatcher(Type type, Collection<String> list) {
        this.type = type;
        this.addAll(list);
    }

    public StringMatcher(Type mode) {
        this.type = mode;
    }

    public StringMatcher(String mode) {
        this(Type.parse(mode));
    }

    public Type getType() { return type; }

    public T getDataHandler() {
        return data;
    }

    public void connectDataHandler(T data) {
        this.data = data;
    }

    public boolean allowed(String element) {
        if(element == null) return true;
        switch(type) {
            case WHITELIST: return containsIgnoreCase(element);
            case BLACKLIST: return !containsIgnoreCase(element);
            default: return true;
        }
    }

    private boolean containsIgnoreCase(String element) {
        for(String string : this)
            if(element.equalsIgnoreCase(string)) return true;
        return false;
    }

}
