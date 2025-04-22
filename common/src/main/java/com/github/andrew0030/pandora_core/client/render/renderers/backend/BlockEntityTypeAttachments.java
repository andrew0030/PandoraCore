package com.github.andrew0030.pandora_core.client.render.renderers.backend;

import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.client.render.renderers.registry.InstancedBERendererRegistry;
import org.jetbrains.annotations.ApiStatus;

/**
 * <strong>DO NOT USE</strong><br/>
 * Refer to {@link InstancedBERendererRegistry} instead!
 */
@ApiStatus.Internal
@Deprecated(forRemoval = false)
public interface BlockEntityTypeAttachments {
    void pandoraCore$setInstancedRenderer(InstancedBlockEntityRenderer<?> renderer);
    InstancedBlockEntityRenderer<?> pandoraCore$getInstancedRenderer();
}
