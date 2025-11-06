package com.github.andrew0030.pandora_core.mixin.render.instancing.compat.iris;

import com.github.andrew0030.pandora_core.client.render.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.SodiumRendererAccessor;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoShadowRendererAccessor;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(value = ShadowRenderingState.class, remap = false)
public abstract class ShadowPassMixin {
    @Inject(at = @At("HEAD"), method = "renderBlockEntities")
    private static void preRenderBEs(ShadowRenderer shadowRenderer, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, Camera camera, double cameraX, double cameraY, double cameraZ, float tickDelta, boolean hasEntityFrustum, boolean lightsOnly, CallbackInfoReturnable<Integer> cir) {
        ClientLevel level = ((IPaCoShadowRendererAccessor) shadowRenderer).getActiveLevel();
        LevelRendererAccessor levelRenderer = ((IPaCoShadowRendererAccessor) shadowRenderer).getRenderer();

        SodiumWorldRenderer renderer = ((WorldRendererExtended) levelRenderer).sodium$getWorldRenderer();

        Lighting.setupLevel(RenderSystem.getModelViewMatrix());
        RenderSystem.setupShaderLights(
                GameRenderer.getRendertypeEntitySolidShader()
        );

        PacoInstancingLevel instancingLevel = (PacoInstancingLevel) level;
        InstanceManager manager = instancingLevel.getManager();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(modelView.last().pose());
        RenderSystem.getModelViewStack().last().normal().mul(modelView.last().normal());
        RenderSystem.getModelViewStack().translate(
                -camera.getPosition().x,
                -camera.getPosition().y,
                -camera.getPosition().z
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
                            be, camera.getPosition()
                    )) {
                        beRenderer.render(level, be, be.getBlockPos());
                    }
                }
            }
        });
        manager.drawFrame(level);

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
