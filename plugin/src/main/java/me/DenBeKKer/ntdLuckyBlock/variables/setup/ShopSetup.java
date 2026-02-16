package me.DenBeKKer.ntdLuckyBlock.variables.setup;

public class ShopSetup {

    private final boolean enabled;
    private final double price;

    public ShopSetup(boolean enabled, double price) {
        this.enabled = enabled;
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getPrice() {
        return price;
    }
}
