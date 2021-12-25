package me.DenBeKKer.ntdLuckyBlock.api;

import me.DenBeKKer.ntdLuckyBlock.util.MvLogger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.logging.Level;

@Deprecated
public class LuckyBlockBreakEvent extends Event {

    @Override
    public HandlerList getHandlers() {
        throw new UnsupportedOperationException();
    }

    static {
        MvLogger.log(Level.WARNING, "me.DenBeKKer.ntdLuckyBlock.api.LuckyBlockBreakEvent is unsupported since build 75. " +
                "Use me.DenBeKKer.ntdLuckyBlock.api.events.LuckyBlockBreakEvent");
    }

}
