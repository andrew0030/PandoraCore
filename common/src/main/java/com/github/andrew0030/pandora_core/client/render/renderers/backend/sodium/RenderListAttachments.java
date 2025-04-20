package com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium;

import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface RenderListAttachments {
    public @Nullable ByteIterator sectionsWithInstancableBEsIterator(boolean reverse);
}
