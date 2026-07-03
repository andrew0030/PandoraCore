package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.sodium;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.sodium.RenderSectionAttachments;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import me.jellysquid.mods.sodium.client.util.iterator.ReversibleByteArrayIterator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkRenderList.class, remap = false)
public class RenderListMixin implements RenderListAttachments {
    @Unique private final byte[] pandoraCore$sectionsWithInstancableBEs = new byte[256];
    @Unique private int pandoraCore$sectionsWithInstancableBEsCount = 0;

    public @Nullable ByteIterator sectionsWithInstancableBEsIterator(boolean reverse) {
        return this.pandoraCore$sectionsWithInstancableBEsCount == 0 ? null : new ReversibleByteArrayIterator(this.pandoraCore$sectionsWithInstancableBEs, this.pandoraCore$sectionsWithInstancableBEsCount, reverse);
    }

    @Inject(at = @At("TAIL"), method = "add")
    public void postAdd(RenderSection render, CallbackInfo ci) {
        this.pandoraCore$sectionsWithInstancableBEs[this.pandoraCore$sectionsWithInstancableBEsCount] = (byte)render.getSectionIndex();
        this.pandoraCore$sectionsWithInstancableBEsCount += ((RenderSectionAttachments) render).hasInstancedBlockEntities() ? 1 : 0;
    }

    @Inject(at = @At("TAIL"), method = "reset")
    public void postRest(int frame, CallbackInfo ci) {
        pandoraCore$sectionsWithInstancableBEsCount = 0;
    }
}