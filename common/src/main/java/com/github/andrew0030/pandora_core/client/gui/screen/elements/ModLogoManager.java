package com.github.andrew0030.pandora_core.client.gui.screen.elements;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class ModLogoManager implements Closeable {
    private final Map<String, Pair<ResourceLocation, DynamicTexture>> modLogoCache = new HashMap<>();

    @Override
    public void close() {
        for (Map.Entry<String, Pair<ResourceLocation, DynamicTexture>> entry : this.modLogoCache.entrySet()) {
            entry.getValue().getSecond().close();
            PandoraCore.LOGGER.info("Removing cache entry for '{}' mod icon...", entry.getKey());
        }
    }

    public boolean isPresent(String modId) {
        return this.modLogoCache.containsKey(modId);
    }

    public DynamicTexture getCachedTexture(String modId) {
        return this.modLogoCache.get(modId).getSecond();
    }

    public ResourceLocation getCachedLocation(String modId) {
        return this.modLogoCache.get(modId).getFirst();
    }

    public void cacheModLogo(String modId, ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
        this.modLogoCache.put(modId, new Pair<>(resourceLocation, dynamicTexture));
        PandoraCore.LOGGER.info("No cache entry for '{}' mod icon, generating...", modId);
    }
}