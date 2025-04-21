package com.github.andrew0030.pandora_core.mixin.render.instancing.compat;

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
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.SortedSet;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class SodiumRendererMixin implements SodiumRendererAccessor {
    @Shadow
    private ClientLevel world;

    @Shadow
    private RenderSectionManager renderSectionManager;

    @Inject(at = @At("HEAD"), method = "renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;Lnet/minecraft/client/Camera;F)V")
    public void preRenderBEs(PoseStack matrices, RenderBuffers bufferBuilders, Long2ObjectMap<SortedSet<BlockDestructionProgress>> blockBreakingProgressions, Camera camera, float tickDelta, CallbackInfo ci) {
        PacoInstancingLevel instancingLevel = (PacoInstancingLevel) world;
        InstanceManager manager = instancingLevel.getManager();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(matrices.last().pose());
        RenderSystem.getModelViewStack().last().normal().mul(matrices.last().normal());
        RenderSystem.getModelViewStack().translate(
                -camera.getPosition().x,
                -camera.getPosition().y,
                -camera.getPosition().z
        );
        RenderSystem.applyModelViewMatrix();

        manager.markFrame();
        SortedRenderLists renderLists = this.renderSectionManager.getRenderLists();
        Iterator<ChunkRenderList> renderListIterator = renderLists.iterator();
        renderListIterator.forEachRemaining(renderList -> {
            ByteIterator iterator = ((RenderListAttachments) renderList).sectionsWithInstancableBEsIterator(false);
            if (iterator == null) return;

            while (iterator.hasNext()) {
                int element = iterator.nextByteAsInt();
                RenderSection section = renderList.getRegion().getSection(element);

                for (BlockEntity be : ((InstancingResults) section).getAll()) {
                    InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
                    if (renderer.shouldRender(
                            be, camera.getPosition()
                    )) {
                        renderer.render(world, be, be.getBlockPos());
                    }
                }
            }
        });
        manager.drawFrame(world);

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public RenderSectionManager pandoraCore$sectionManager() {
        return renderSectionManager;
    }
}
