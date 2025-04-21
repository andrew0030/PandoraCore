package com.github.andrew0030.pandora_core.mixin.render.instancing.compat.iris;

import com.github.andrew0030.pandora_core.client.render.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.SodiumRendererAccessor;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.SortedSet;

@Mixin(value = ShadowRenderer.class, remap = false)
public abstract class ShadowPassMixin {
    @Shadow
    protected static ClientLevel getLevel() {
        return null;
    }

    PoseStack cptureStk;

    @Inject(at = @At("HEAD"), method = "renderEntities")
    public void captureMatrices0(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Integer> cir) {
        cptureStk = modelView;
    }

    @Inject(at = @At("HEAD"), method = "renderPlayerEntity")
    public void captureMatrices1(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Integer> cir) {
        cptureStk = modelView;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shadows/ShadowRenderingState;renderBlockEntities(Lnet/irisshaders/iris/shadows/ShadowRenderer;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;DDDFZZ)I", shift = At.Shift.AFTER), method = "renderShadows")
    public void preRenderBEs(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        SodiumWorldRenderer renderer = ((WorldRendererExtended) levelRenderer).sodium$getWorldRenderer();

        PacoInstancingLevel instancingLevel = (PacoInstancingLevel) getLevel();
        InstanceManager manager = instancingLevel.getManager();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(cptureStk.last().pose());
        RenderSystem.getModelViewStack().last().normal().mul(cptureStk.last().normal());
        RenderSystem.getModelViewStack().translate(
                -playerCamera.getPosition().x,
                -playerCamera.getPosition().y,
                -playerCamera.getPosition().z
        );
        RenderSystem.applyModelViewMatrix();

        manager.markFrame();
        SortedRenderLists renderLists = ((SodiumRendererAccessor) renderer).pandoraCore$sectionManager().getRenderLists();
        Iterator<ChunkRenderList> renderListIterator = renderLists.iterator();
        renderListIterator.forEachRemaining(renderList -> {
            ByteIterator iterator = ((RenderListAttachments) renderList).sectionsWithInstancableBEsIterator(false);
            if (iterator == null) return;

            while (iterator.hasNext()) {
                int element = iterator.nextByteAsInt();
                RenderSection section = renderList.getRegion().getSection(element);

                for (BlockEntity be : ((InstancingResults) section).getAll()) {
                    InstancedBlockEntityRenderer beRenderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
                    if (beRenderer.shouldRender(
                            be, playerCamera.getPosition()
                    )) {
                        beRenderer.render(getLevel(), be, be.getBlockPos());
                    }
                }
            }
        });
        manager.drawFrame(getLevel());

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
