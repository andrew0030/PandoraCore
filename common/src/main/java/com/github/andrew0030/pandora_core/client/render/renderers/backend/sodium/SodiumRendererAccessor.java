package com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Deprecated
public interface SodiumRendererAccessor {
    RenderSectionManager pandoraCore$sectionManager();
}
