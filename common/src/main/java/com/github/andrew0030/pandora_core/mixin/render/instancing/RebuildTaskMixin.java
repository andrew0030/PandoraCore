package com.github.andrew0030.pandora_core.mixin.render.instancing;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class RebuildTaskMixin {
    @Inject(at = @At("HEAD"), method = "handleBlockEntity")
    public <E extends BlockEntity> void preAddBE(ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults pCompileResults, E pBlockEntity, CallbackInfo ci) {
        InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) pBlockEntity.getType()).pandoraCore$getInstancedRenderer();
        if (renderer != null) {
            ((InstancingResults) (Object) pCompileResults).addInstancer(pBlockEntity);
        }
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;renderableBlockEntities:Ljava/util/List;"), method = "doTask", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void copy(ChunkBufferBuilderPack pBuffers, CallbackInfoReturnable<CompletableFuture> cir, Vec3 vec3, float f, float f1, float f2, ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults chunkrenderdispatcher$renderchunk$rebuildtask$compileresults, ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk) {
        ((InstancingResults) chunkrenderdispatcher$compiledchunk).addAll(((InstancingResults) (Object) chunkrenderdispatcher$renderchunk$rebuildtask$compileresults).getAll());
    }
}
