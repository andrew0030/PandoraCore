package com.github.andrew0030.pandora_core.utils.update_checker;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.update_checker.strategies.ModrinthUpdateStrategy;
import com.github.andrew0030.pandora_core.utils.update_checker.strategies.UpdateCheckStrategy;
import com.github.andrew0030.pandora_core.utils.update_checker.strategies.UrlUpdateStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PaCoUpdateChecker {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "UpdateChecker");
    private static final int MAX_THREADS = 5; // TODO add a config option for this
    private static final ThreadPoolExecutor EXECUTOR = new PaCoUpdateExecutor(MAX_THREADS, 10L, TimeUnit.SECONDS, r -> {
        Thread t = new Thread(r, "PaCoUpdateChecker");
        t.setDaemon(true);
        return t;
    });

    public static void checkForUpdates() {
        Set<ModDataHolder> modrinthHolders = new HashSet<>();
        Set<ModDataHolder> urlHolders = new HashSet<>();
        for (ModDataHolder holder : PandoraCore.getModHolders()) {
            if (holder.getUpdateURL().isPresent()) {
                urlHolders.add(holder);
            } else {
                modrinthHolders.add(holder);
            }
        }
        LOGGER.info("Checking for updates ({} threads): {} Modrinth checks, {} URL-based checks", MAX_THREADS, modrinthHolders.size(), urlHolders.size());
        EXECUTOR.submit(() -> PaCoUpdateChecker.performCheck(new ModrinthUpdateStrategy(modrinthHolders)));
        for (ModDataHolder holder : urlHolders)
            EXECUTOR.submit(() -> PaCoUpdateChecker.performCheck(new UrlUpdateStrategy(holder)));
    }

    /** Initializes the update check on the given {@link UpdateCheckStrategy} */
    private static void performCheck(UpdateCheckStrategy strategy) {
        strategy.performUpdateCheck();
    }

    /**
     * This {@link ThreadPoolExecutor} is intended to run up to
     * {@code poolSize} tasks in parallel, and when done terminate all threads.
     * <p>
     * The way this "on demand parallelism" is achieved is by:
     * <ul>
     *     <li>Having the {@code workQueue} be a {@link LinkedBlockingQueue} for "unlimited" queue size.</li>
     *     <li>Having {@code corePoolSize} set to the same value as {@code maxPoolSize}, so the {@code workQueue} can utilize multiple threads.</li>
     *     <li>Having core threads time out just like non-core threads.</li>
     * </ul>
     */
    private static class PaCoUpdateExecutor extends ThreadPoolExecutor {
        public PaCoUpdateExecutor(@Range(from = 1, to = Integer. MAX_VALUE)  int poolSize,
                                  @Range(from = 0, to = Long. MAX_VALUE)  long keepAliveTime,
                                  @NotNull TimeUnit unit,
                                  @NotNull ThreadFactory threadFactory) {
            super(poolSize, poolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(), threadFactory);
            this.allowCoreThreadTimeOut(true);
        }
    }
}