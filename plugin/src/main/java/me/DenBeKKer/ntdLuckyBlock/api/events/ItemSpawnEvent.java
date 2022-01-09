package me.DenBeKKer.ntdLuckyBlock.api.events;

import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemSpawnEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    private final LBMain.LuckyBlockType r;
    private final Item i;
    private final Player p;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ItemSpawnEvent(LBMain.LuckyBlockType related, Item item, Player player) {

        this.r = related;
        this.i = item;
        this.p = player;

    }

    public Player getPlayer() { return p; }

    public LBMain.LuckyBlockType getSource() {
        return r;
    }

    public Item getItem() {
        return i;
    }

}
