package com.github.andrew0030.pandora_core.utils.update_checker;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.update_checker.strategies.ModrinthUpdateStrategy;
import com.github.andrew0030.pandora_core.utils.update_checker.strategies.UpdateCheckStrategy;
import com.github.andrew0030.pandora_core.utils.update_checker.strategies.UrlUpdateStrategy;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaCoUpdateChecker {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoUpdateChecker");
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "PaCoUpdateChecker");
        thread.setDaemon(true);
        return thread;
    });

    public static void checkForUpdates() {
        Set<ModDataHolder> urlHolders = new HashSet<>();
        Set<ModDataHolder> modrinthHolders = new HashSet<>();
        for (ModDataHolder holder : PandoraCore.getModHolders()) {
            if (holder.getUpdateURL().isPresent()) {
                urlHolders.add(holder);
            } else {
                modrinthHolders.add(holder);
            }
        }
        EXECUTOR.submit(() -> PaCoUpdateChecker.performCheck(new UrlUpdateStrategy(urlHolders)));
        EXECUTOR.submit(() -> PaCoUpdateChecker.performCheck(new ModrinthUpdateStrategy(modrinthHolders)));
    }

    private static void performCheck(UpdateCheckStrategy strategy) {
        strategy.performUpdateCheck();
    }
}