package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class ModIconManager implements Closeable {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ModIconManager");
    private final Map<String, Pair<ResourceLocation, DynamicTexture>> modIconCache = new HashMap<>();
    // Just a boolean that can be toggled to get some debug info about icon caching.
    public static final boolean SHOW_DEBUG_MESSAGES = false;

    @Override
    public void close() {
        for (Map.Entry<String, Pair<ResourceLocation, DynamicTexture>> entry : this.modIconCache.entrySet()) {
            // If a mod failed loading, or its logo isn't 1:1 there is no DynamicTexture that needs closing.
            if (entry.getValue().getSecond() != null)
                entry.getValue().getSecond().close();
            PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "Removing mod icon cache entry for '{}'", entry.getKey());
        }
    }

    public boolean isPresent(String modId) {
        return this.modIconCache.containsKey(modId);
    }

    public Pair<ResourceLocation, DynamicTexture> getCachedEntry(String modId) {
        return this.modIconCache.get(modId);
    }

    public void cacheModIcon(String modId, ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
        this.modIconCache.put(modId, new Pair<>(resourceLocation, dynamicTexture));
        PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "No mod icon cache entry for '{}', generating...", modId);
    }
}