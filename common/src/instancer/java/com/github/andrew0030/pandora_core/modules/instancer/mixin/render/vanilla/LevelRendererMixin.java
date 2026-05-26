package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.vanilla;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
	    PaCoRenderState.setupWorld();
		
        Lighting.setupLevel(RenderSystem.getModelViewMatrix());
        RenderSystem.setupShaderLights(
                GameRenderer.getRendertypeEntitySolidShader()
        );

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
                if (renderer.shouldRender(
                        be, $$4.getPosition()
                )) {
                    renderer.render(level, be, be.getBlockPos(), $$1);
                }
            }
        }
        manager.drawFrame(level);

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
	    
	    PaCoRenderState.resetInstancerState();
    }
}
