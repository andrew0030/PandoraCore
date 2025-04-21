package com.github.andrew0030.pandora_core.mixin.render.instancing;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Final private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;

    @Shadow @Nullable private ClientLevel level;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderedEntities:I", ordinal = 0), method = "renderLevel")
    public void preRenderEnts(PoseStack stack, float $$1, long $$2, boolean $$3, Camera $$4, GameRenderer $$5, LightTexture $$6, Matrix4f $$7, CallbackInfo ci) {
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
        RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
        RenderSystem.getModelViewStack().translate(
                -$$4.getPosition().x,
                -$$4.getPosition().y,
                -$$4.getPosition().z
        );
        RenderSystem.applyModelViewMatrix();

        InstanceManager manager = ((PacoInstancingLevel)level).getManager();
        manager.markFrame();
        for (LevelRenderer.RenderChunkInfo info : this.renderChunksInFrustum) {
            ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
            for (BlockEntity be : ((InstancingResults) chnk).getAll()) {
                InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments)be.getType()).pandoraCore$getInstancedRenderer();
                renderer.render(level, be, be.getBlockPos());
            }
        }
        manager.drawFrame(level);

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
