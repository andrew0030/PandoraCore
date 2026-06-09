package com.github.andrew0030.pandora_core.modules.instancer.renderers.backend;

import com.github.andrew0030.pandora_core.modules.instancer.registry.InstancedRendererRegistry;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import org.jetbrains.annotations.ApiStatus;

/**
 * <strong>DO NOT USE</strong><br/>
 * Refer to {@link InstancedRendererRegistry} instead!
 */
@ApiStatus.Internal
@Deprecated(forRemoval = false)
public interface EntityTypeAttachments {
    void pandoraCore$setInstancedRenderer(InstancedEntityRenderer<?> renderer);
	InstancedEntityRenderer<?> pandoraCore$getInstancedRenderer();
}
