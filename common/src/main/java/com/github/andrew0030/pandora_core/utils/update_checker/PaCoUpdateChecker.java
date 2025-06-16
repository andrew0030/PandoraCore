package com.github.andrew0030.pandora_core.utils.update_checker;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.update_checker.check_typs.BaseCheckType;
import com.github.andrew0030.pandora_core.utils.update_checker.check_typs.ModrinthCheckType;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaCoUpdateChecker {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoUpdateChecker");
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "PaCoUpdateChecker");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Used to queue an update check for a given mod.
     */
    public static void checkForUpdates() {
//        Collection<ModDataHolder> holders = PandoraCore.getModHolders();
//        for (ModDataHolder holder : holders)
        EXECUTOR.submit(() -> PaCoUpdateChecker.performCheck(PandoraCore.getModHolder("skinlayers3d")));
    }

    private static void performCheck(ModDataHolder holder) {
        BaseCheckType checkType = new ModrinthCheckType(holder);
        checkType.performUpdateCheck();
    }
}