package com.github.danirod12.luckyblock.util.random;

import com.github.danirod12.luckyblock.api.model.random.Amount;
import com.github.danirod12.luckyblock.api.model.random.LuckyCollection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class WeightListAmount<T> extends WeightList<T> implements LuckyCollection<T> {
    private Amount amount;
    private String permission;

    public WeightListAmount() {
        this.amount = new Amount(1);
    }

    public WeightListAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public Set<T> getAll() {
        int amount = this.amount.get();
        if (amount > super.size()) {
            amount = super.size();
        }

        Set<T> result = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            result.add(super.get());
        }
        return result;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission == null || permission.isEmpty()
                || permission.equalsIgnoreCase("none") ? null : permission;
    }

    @Override
    public boolean hasPermission(Player player) {
        return this.permission == null || player.hasPermission(this.permission);
    }
}
