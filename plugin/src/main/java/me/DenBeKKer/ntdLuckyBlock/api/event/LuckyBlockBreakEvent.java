package me.DenBeKKer.ntdLuckyBlock.api.event;

import lombok.Getter;
import lombok.Setter;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
public class LuckyBlockBreakEvent extends CancellableEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Block block;
    private final Player player;
    private final LuckyBlock luckyBlock;
    @Setter
    private boolean drop = true;

    public LuckyBlockBreakEvent(Block block, Player player, LuckyBlock luckyBlock) {
        this.block = block;
        this.player = player;
        this.luckyBlock = luckyBlock;
    }

    public LuckyBlockBreakEvent(Block block, LuckyBlock luckyBlock) {
        this(block, null, luckyBlock);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public boolean isPlayerInvolved() {
        return player != null;
    }
}
