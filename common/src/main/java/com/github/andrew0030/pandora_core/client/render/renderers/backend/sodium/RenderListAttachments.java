package com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium;

import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
@Deprecated
public interface RenderListAttachments {
    public @Nullable ByteIterator sectionsWithInstancableBEsIterator(boolean reverse);
}
