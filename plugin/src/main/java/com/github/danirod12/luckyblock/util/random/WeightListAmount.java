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
public class WeightListAmount<T> extends WeightListB<T, String> implements LuckyCollection<T> {
    private Amount amount;

    public WeightListAmount() {
        this.amount = new Amount(1);
    }

    public WeightListAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public Set<T> rollItems(Player player) {
        return new HashSet<>(super.get(this.amount.get(), player == null ? null
                : (node, bind) -> bind == null || player.hasPermission(bind))
        );
    }
}
