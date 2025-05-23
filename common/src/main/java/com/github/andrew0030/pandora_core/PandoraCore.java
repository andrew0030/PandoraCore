package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.config.PaCoMainConfig;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.registry.PaCoBlockEntities;
import com.github.andrew0030.pandora_core.registry.PaCoBlocks;
import com.github.andrew0030.pandora_core.registry.PaCoCreativeModeTabs;
import com.github.andrew0030.pandora_core.registry.PaCoItems;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;

public class PandoraCore {
    public static final String MOD_ID = "pandora_core";
    public static final String MOD_NAME = "Pandora Core";
    private static final Logger LOGGER = PaCoLogger.create(MOD_NAME);
    private static final HashMap<String, ModDataHolder> MOD_HOLDERS = new HashMap<>();

    /** Early Init (Mod Construction) **/
    public static void earlyInit() {
        PaCoBlocks.BLOCKS.register();
        PaCoItems.ITEMS.register();
        PaCoBlockEntities.BLOCK_ENTITY_TYPES.register();
        PaCoCreativeModeTabs.CREATIVE_MODE_TABS.register();
    }

    /** Common Init */
    public static void init() {
        Services.PLATFORM.getModDataHolders().forEach(holder -> PandoraCore.MOD_HOLDERS.put(holder.getModId(), holder));

        // Configs
        PaCoConfigManager.register(PaCoMainConfig.class);

        // Inserts items into existing tabs
        // TODO remove when done testing
        PaCoCreativeModeTabs.insertItems();

        // Calls FileConfig#close() on all registered configs when the game shuts down
        Runtime.getRuntime().addShutdownHook(new Thread(PaCoConfigManager::closeConfigs));
    }

    /** Thread Safe Common Init */
    public static void initThreadSafe() {

    }

    /** A {@link Collection} containing a {@link ModDataHolder} for each loaded mod. */
    public static Collection<ModDataHolder> getModHolders() {
        return PandoraCore.MOD_HOLDERS.values();
    }

    /**
     * @param id The mod id used to retrieve the associated data holder.
     * @return A {@link ModDataHolder} containing information about the mod that was requested.
     */
    @Nullable
    public static ModDataHolder getModHolder(String id) {
        return PandoraCore.MOD_HOLDERS.get(id);
    }
}