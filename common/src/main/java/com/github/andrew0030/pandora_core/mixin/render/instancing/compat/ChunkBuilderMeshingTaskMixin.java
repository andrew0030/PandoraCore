package com.github.andrew0030.pandora_core.mixin.render.instancing.compat;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(value = ChunkBuilderMeshingTask.class)
public class ChunkBuilderMeshingTaskMixin {
    // shut up the mcdev plugin
    @SuppressWarnings({
            "InvalidInjectorMethodSignature",
            "UnresolvedMixinReference"
    })
    @Inject(
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void redirAddBE(
            ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
            BuiltSectionInfo.Builder renderData, VisGraph occluder, ChunkBuildBuffers buffers, BlockRenderCache cache, WorldSlice slice,
            int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
            BlockPos.MutableBlockPos blockPos, BlockPos.MutableBlockPos modelOffset,
            BlockRenderContext context,
            int y, int z, int x,
            BlockState blockState, FluidState fluidState, BlockEntity entity
//            long var23, Iterator var25, RenderType var26
    ) {
        InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) entity.getType()).pandoraCore$getInstancedRenderer();
        if (renderer != null) {
            ((InstancingResults) renderData).addInstancer(entity);
        }
    }
}
