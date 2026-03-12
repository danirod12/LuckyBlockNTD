package com.github.danirod12.luckyblock.command.base;

import org.stringtemplate.v4.misc.Misc;

import java.util.Arrays;
import java.util.Optional;

public class BaseAlias {

    private final String[][] from;
    private final String redirection;

    public BaseAlias(String[][] from, String redirection) {
        this.from = from;
        this.redirection = redirection;
    }

    public static BaseAlias build(String from, String to) {
        String[] flat = from.split(" ");
        String[][] key = new String[flat.length][];
        for (int i = 0; i < flat.length; i++) {
            key[i] = new String[]{flat[i]};
        }
        return new BaseAlias(key, to);
    }

    public Optional<String> getRedirection(String[] args) {
        if (args.length < from.length) {
            return Optional.empty();
        }
        loop:
        for (int i = 0; i < from.length; i++) {
            for (String key : from[i]) {
                if (key.equalsIgnoreCase(args[i])) {
                    continue loop;
                }
            }
            return Optional.empty();
        }
        return Optional.of(redirection + Misc.join(Arrays.stream(args)
                .skip(from.length).iterator(), " "));
    }
}
