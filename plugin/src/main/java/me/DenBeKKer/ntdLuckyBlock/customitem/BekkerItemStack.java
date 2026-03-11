package me.DenBeKKer.ntdLuckyBlock.customitem;

import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.event.CustomItemHandleEvent;
import me.DenBeKKer.ntdLuckyBlock.api.model.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public class BekkerItemStack extends ItemStack {

    @Getter
    private final Identifier identifier;
    private final Map<ItemEvent<?>, Consumer<Event>> map;

    public BekkerItemStack(Identifier identifier, ItemStack stack, Map<ItemEvent<?>, Consumer<Event>> map) {
        super(identifier.apply(stack));
        this.identifier = identifier;
        this.map = map;
    }

    public void register() {
        CustomItemFactory.register(this);
    }

    @Override
    public boolean equals(Object object) {
        return identifier.equals(object);
    }

    public Set<ItemEvent<?>> getEvents() {
        return map.keySet();
    }

    public void handle(Event event) {
//        LBMain.debug(event.getClass().getSimpleName() + " - " + identifier.getIdentifier());
        for (Entry<ItemEvent<?>, Consumer<Event>> element : map.entrySet()) {
            if (element.getKey().getInstance().isAssignableFrom(event.getClass())) {
                CustomItemHandleEvent handleEvent = new CustomItemHandleEvent(this, event, true);
                Bukkit.getPluginManager().callEvent(handleEvent);
                if (handleEvent.isCancelled()) {
                    return;
                }

                element.getValue().accept(event);
                return;
            }
        }
        Bukkit.getPluginManager().callEvent(new CustomItemHandleEvent(this, event, false));
    }
}
