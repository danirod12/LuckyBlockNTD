package me.DenBeKKer.ntdLuckyBlock.api.exception;

public class EntryFormatException extends Exception {

    public EntryFormatException(String configName, String path) {
        super("Incorrect entry format (Path not found or zero items loaded) " + configName + ", " + path);
    }
}
