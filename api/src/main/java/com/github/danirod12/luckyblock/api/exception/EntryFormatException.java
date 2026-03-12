package com.github.danirod12.luckyblock.api.exception;

public class EntryFormatException extends Exception {

    public EntryFormatException(String configName, String path) {
        super("Incorrect entry format (Path not found or zero items loaded) " + configName + ", " + path);
    }
}
