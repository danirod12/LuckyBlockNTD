package com.github.danirod12.luckyblock.api.model.random;

import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Amount {
    public static final Amount MAX_VALUE = new Amount(Integer.MAX_VALUE);
    public static final Amount ONE = new Amount(1);

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
        if (amount == null || amount.isEmpty() || amount.equals("1")) {
            return ONE;
        }

        String[] data = amount.split("-");
        if (data[0].equalsIgnoreCase("ALL")) {
            return MAX_VALUE;
        }

        int from = Integer.parseInt(data[0]);
        return new Amount(from, data.length > 1 ? Integer.parseInt(data[1]) : from);
    }

    @Override
    public String toString() {
        if (this == MAX_VALUE) {
            return "ALL";
        }
        return from == to ? String.valueOf(to) : from + "-" + to;
    }

    public boolean isFixed() {
        return from == to;
    }
}
