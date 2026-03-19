package com.github.danirod12.luckyblock.api.model.random;

import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Amount {
    private final int from;
    private final int to;

    public Amount(int from, int to) {
        this.from = Math.min(from, to);
        this.to = Math.max(from, to);
    }

    public Amount(int of) {
        this(of, of);
    }

    public int get() {
        if (this.isFixed()) {
            return from;
        }
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static Amount of(String amount) throws NumberFormatException {
        String[] data = amount.split("-");
        int from = Integer.parseInt(data[0]);
        return new Amount(from, data.length > 1 ? Integer.parseInt(data[1]) : from);
    }

    @Override
    public String toString() {
        return from == to ? String.valueOf(to) : from + "-" + to;
    }

    public boolean isFixed() {
        return from == to;
    }
}
