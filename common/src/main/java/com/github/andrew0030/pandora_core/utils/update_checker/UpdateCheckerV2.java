package com.github.andrew0030.pandora_core.utils.update_checker;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateCheckerV2 {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "UpdateChecker");
    private static final int HTTP_TIMEOUT_SECS = 15;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "UpdateChecker");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Used to queue an update check for a given mod.
     */
    public static void checkForUpdate(ModDataHolder holder) {
        EXECUTOR.submit(() -> UpdateCheckerV2.performCheck(holder));
    }

    private static void performCheck(ModDataHolder holder) {

    }
}