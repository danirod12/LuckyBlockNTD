package com.github.danirod12.luckyblock.api.folia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class SchedulerManager {

    private static final boolean IS_FOLIA;

    private static Object regionScheduler;
    private static Object globalRegionScheduler;
    private static Object asyncScheduler;

    private static Method regionExecute;
    private static Method regionRunDelayed;
    private static Method regionRunAtFixedRate;

    private static Method globalExecute;
    private static Method globalRunDelayed;
    private static Method globalRunAtFixedRate;

    private static Method asyncRunDelayed;
    private static Method asyncRunAtFixedRate;

    private static Method taskCancel;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) { }
        IS_FOLIA = folia;

        if (IS_FOLIA) {
            try {
                regionScheduler = Bukkit.class.getMethod("getRegionScheduler").invoke(null);
                globalRegionScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                asyncScheduler = Bukkit.class.getMethod("getAsyncScheduler").invoke(null);

                Class<?> regionClass = Class
                        .forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
                regionExecute = regionClass.getMethod("execute", Plugin.class, Location.class, Runnable.class);
                regionRunDelayed = regionClass.getMethod("runDelayed", Plugin.class,
                        Location.class, Consumer.class, long.class);
                regionRunAtFixedRate = regionClass.getMethod("runAtFixedRate", Plugin.class,
                        Location.class, Consumer.class, long.class, long.class);

                Class<?> globalClass = Class
                        .forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
                globalExecute = globalClass.getMethod("execute", Plugin.class, Runnable.class);
                globalRunDelayed = globalClass.getMethod("runDelayed", Plugin.class,
                        Consumer.class, long.class);
                globalRunAtFixedRate = globalClass.getMethod("runAtFixedRate", Plugin.class,
                        Consumer.class, long.class, long.class);

                Class<?> asyncClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
                asyncRunDelayed = asyncClass.getMethod("runDelayed", Plugin.class,
                        Consumer.class, long.class, java.util.concurrent.TimeUnit.class);
                asyncRunAtFixedRate = asyncClass.getMethod("runAtFixedRate", Plugin.class,
                        Consumer.class, long.class, long.class, java.util.concurrent.TimeUnit.class);

                Class<?> taskClass = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
                taskCancel = taskClass.getMethod("cancel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static void cancelFoliaTask(Object task) {
        try {
            if (task != null) {
                taskCancel.invoke(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runAt(Plugin plugin, Location location, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                regionExecute.invoke(regionScheduler, plugin, location, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runLaterAt(Plugin plugin, Location location, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            try {
                Consumer<Object> consumer = task -> runnable.run();
                regionRunDelayed.invoke(regionScheduler, plugin, location, consumer, Math.max(1L, delay));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void runTimerAt(Plugin plugin, Location location, ManagedRunnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            try {
                Consumer<Object> consumer = task -> {
                    if (runnable.isCancelled()) {
                        cancelFoliaTask(task);
                        return;
                    }
                    runnable.setFoliaTask(task);
                    runnable.run();
                };
                Object taskObj = regionRunAtFixedRate.invoke(regionScheduler, plugin, location,
                        consumer, Math.max(1L, delay), Math.max(1L, period));
                runnable.setFoliaTask(taskObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
            runnable.setBukkitTask(task);
        }
    }

    public static void runGlobal(Plugin plugin, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                globalExecute.invoke(globalRegionScheduler, plugin, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runLaterGlobal(Plugin plugin, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            try {
                Consumer<Object> consumer = task -> runnable.run();
                globalRunDelayed.invoke(globalRegionScheduler, plugin, consumer, Math.max(1L, delay));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void runTimerGlobal(Plugin plugin, ManagedRunnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            try {
                Consumer<Object> consumer = task -> {
                    if (runnable.isCancelled()) {
                        cancelFoliaTask(task);
                        return;
                    }
                    runnable.setFoliaTask(task);
                    runnable.run();
                };
                Object taskObj = globalRunAtFixedRate.invoke(globalRegionScheduler,
                        plugin, consumer, Math.max(1L, delay), Math.max(1L, period));
                runnable.setFoliaTask(taskObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
            runnable.setBukkitTask(task);
        }
    }

    public static void runAsyncLater(Plugin plugin, Runnable runnable, long delayTicks) {
        if (IS_FOLIA) {
            try {
                Consumer<Object> consumer = task -> runnable.run();
                asyncRunDelayed.invoke(asyncScheduler, plugin, consumer,
                        delayTicks * 50L, java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks);
        }
    }

    public static void runAsyncTimer(Plugin plugin, ManagedRunnable runnable, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            try {
                Consumer<Object> consumer = task -> {
                    if (runnable.isCancelled()) {
                        cancelFoliaTask(task);
                        return;
                    }
                    runnable.setFoliaTask(task);
                    runnable.run();
                };
                Object taskObj = asyncRunAtFixedRate.invoke(asyncScheduler, plugin, consumer,
                        delayTicks * 50L, periodTicks * 50L, java.util.concurrent.TimeUnit.MILLISECONDS);
                runnable.setFoliaTask(taskObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable,
                    delayTicks, periodTicks);
            runnable.setBukkitTask(task);
        }
    }
}
