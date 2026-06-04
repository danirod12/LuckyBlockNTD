package com.github.danirod12.luckyblock.api.model.random;

import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an amount of something, which can be a fixed number or a range.
 */
@Getter
public class Amount {
    public static final Amount MAX_VALUE = new Amount(Integer.MAX_VALUE);
    public static final Amount ONE = new Amount(1);

    private final int from;
    private final int to;

    /**
     * Creates an Amount with a range from 'from' to 'to'. The order of 'from' and 'to' does not matter.
     *
     * @param from the lower bound of the range
     * @param to   the upper bound of the range
     */
    public Amount(int from, int to) {
        this.from = Math.min(from, to);
        this.to = Math.max(from, to);
    }

    /**
     * Creates an Amount with a fixed value.
     *
     * @param of the fixed value
     */
    public Amount(int of) {
        this(of, of);
    }

    /**
     * Gets a random value from the range defined by this Amount. If the Amount is fixed, it returns that fixed value.
     *
     * @return a random value from the range or the fixed value
     */
    public int get() {
        if (this.isFixed()) {
            return from;
        }
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    /**
     * Parses a string representation of an Amount. The string can be a single number (e.g., "5"),
     * a range (e.g., "3-7"), or "ALL" for the maximum value.
     *
     * @param amount the string representation of the Amount
     * @return the parsed Amount object
     * @throws NumberFormatException if the string is not in a valid format
     */
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

    /**
     * Returns a string representation of this Amount. If this Amount is the MAX_VALUE, it returns "ALL".
     * If 'from' and 'to' are the same, it returns that single value as a string. Otherwise, it returns "from-to".
     *
     * @return a string representation of this Amount
     */
    @Override
    public String toString() {
        if (this == MAX_VALUE) {
            return "ALL";
        }
        return from == to ? String.valueOf(to) : from + "-" + to;
    }

    /**
     * Checks if this Amount represents a fixed value (i.e., from and to are the same).
     *
     * @return true if this Amount is fixed, false otherwise
     */
    public boolean isFixed() {
        return from == to;
    }
}
