package com.github.andrew0030.pandora_core.client.render.renderers.backend;

import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstanceRenderer;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import org.jetbrains.annotations.ApiStatus;

/**
 * DO NOT USE
 * Refer to {@link com.github.andrew0030.pandora_core.client.render.renderers.registry.InstancedBERendererRegistry} instead
 */
@ApiStatus.Internal
@Deprecated(forRemoval = false)
public interface BlockEntityTypeAttachments {
    void pandoraCore$setInstancedRenderer(InstancedBlockEntityRenderer<?> renderer);
    InstancedBlockEntityRenderer<?> pandoraCore$getInstancedRenderer();
}
