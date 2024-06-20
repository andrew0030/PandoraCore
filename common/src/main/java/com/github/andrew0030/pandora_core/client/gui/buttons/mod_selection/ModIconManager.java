package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class ModIconManager implements Closeable {
    private final Map<String, Pair<ResourceLocation, DynamicTexture>> modIconCache = new HashMap<>();

    @Override
    public void close() {
        for (Map.Entry<String, Pair<ResourceLocation, DynamicTexture>> entry : this.modIconCache.entrySet()) {
            entry.getValue().getSecond().close();
            PandoraCore.LOGGER.info("Removing cache entry for '{}' mod icon...", entry.getKey());
        }
    }

    public boolean isPresent(String modId) {
        return this.modIconCache.containsKey(modId);
    }

    public Pair<ResourceLocation, DynamicTexture> getCachedEntry(String modId) {
        return this.modIconCache.get(modId);
    }

    public void cacheModIcon(String modId, ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
        this.modIconCache.put(modId, new Pair(resourceLocation, dynamicTexture));
        PandoraCore.LOGGER.info("No cache entry for '{}' mod icon, generating...", modId);
    }
}