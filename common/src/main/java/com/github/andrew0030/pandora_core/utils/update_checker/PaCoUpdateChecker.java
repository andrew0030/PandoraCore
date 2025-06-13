package com.github.andrew0030.pandora_core.utils.update_checker;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaCoUpdateChecker {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoUpdateChecker");
    private static final int HTTP_TIMEOUT_SECS = 15;
    private static String PACO_VERSION;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "PaCoUpdateChecker");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Used to queue an update check for a given mod.
     */
    public static void checkForUpdate(ModDataHolder holder) {
        EXECUTOR.submit(() -> PaCoUpdateChecker.performCheck(holder));
    }

    private static void performCheck(ModDataHolder holder) {
        System.out.println(PaCoUpdateChecker.buildUserAgent(holder));
    }

    private static String buildUserAgent(ModDataHolder holder) {
        // Initializes and caches the PaCo version
        if (PACO_VERSION == null) {
            ModDataHolder paco = PandoraCore.getModHolder("hehe");
            if (paco == null) PACO_VERSION = "";
            else if (paco.getModVersion() == null) PACO_VERSION = "";
            else PACO_VERSION = paco.getModVersion();
        }
        // Builds and returns an user agent string
        return String.format(
                "%s (%s; Java/%s; %s) Mod/%s/%s",
                PACO_VERSION.isEmpty() ? "PandoraCore" : "PandoraCore/" + PACO_VERSION,
                Services.PLATFORM.getPlatformName(),
                System.getProperty("java.version"),
                System.getProperty("os.name"),
                holder.getModId(),
                holder.getModVersion()
        );
    }
}