package com.github.andrew0030.pandora_core.mixin.render.instancing.compat;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.RenderSectionAttachments;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import me.jellysquid.mods.sodium.client.util.iterator.ReversibleByteArrayIterator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkRenderList.class, remap = false)
public class RenderListMixin implements RenderListAttachments {
    private final byte[] sectionsWithInstancableBEs = new byte[256];
    private int sectionsWithInstancableBEsCount = 0;

    public @Nullable ByteIterator sectionsWithInstancableBEsIterator(boolean reverse) {
        return this.sectionsWithInstancableBEsCount == 0 ? null : new ReversibleByteArrayIterator(this.sectionsWithInstancableBEs, this.sectionsWithInstancableBEsCount, reverse);
    }

    @Inject(at = @At("TAIL"), method = "add")
    public void postAdd(RenderSection render, CallbackInfo ci) {
        this.sectionsWithInstancableBEs[this.sectionsWithInstancableBEsCount] = (byte)render.getSectionIndex();
        this.sectionsWithInstancableBEsCount += ((RenderSectionAttachments) render).hasInstancedBlockEntities() ? 1 : 0;
    }

    @Inject(at = @At("TAIL"), method = "reset")
    public void postRest(int frame, CallbackInfo ci) {
        sectionsWithInstancableBEsCount = 0;
    }
}
