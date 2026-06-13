package com.github.danirod12.luckyblock.api.folia;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

public abstract class ManagedRunnable implements Runnable {
    @Setter
    private Object foliaTask;
    @Setter
    private BukkitTask bukkitTask;
    @Getter
    private boolean cancelled = false;

    public void cancel() {
        this.cancelled = true;
        if (SchedulerManager.isFolia() && foliaTask != null) {
            SchedulerManager.cancelFoliaTask(foliaTask);
        } else if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }
}
