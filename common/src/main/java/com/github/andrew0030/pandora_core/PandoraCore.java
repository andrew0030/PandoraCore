package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PandoraCore {
    public static final String MOD_ID = "pandora_core";
    public static final String MOD_NAME = "Pandora Core";
    private static final Logger LOGGER = PaCoLogger.create(MOD_NAME);
    private static final List<ModDataHolder> MOD_HOLDERS = new ArrayList<>();

    /** Common Init */
    public static void init() {
        PandoraCore.MOD_HOLDERS.addAll(Services.PLATFORM.getModDataHolders());
    }

    /** Thread Safe Common Init */
    public static void initThreadSafe() {

    }

    /** A {@link List} containing a {@link ModDataHolder} for each loaded mod. */
    public static List<ModDataHolder> getModHolders() {
        return PandoraCore.MOD_HOLDERS;
    }
}