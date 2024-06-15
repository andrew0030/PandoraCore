package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.github.andrew0030.pandora_core.client.shader.holder.PostChainHolder;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private Camera mainCamera;

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    public void initPaCoPostShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        this.loadPaCoPostShaders(resourceProvider);
    }

    @Inject(method = "close", at = @At("TAIL"))
    public void closePaCoPostShaders(CallbackInfo ci) {
        for (PostChainHolder holder : PaCoPostShaderRegistry.POST_SHADERS)
            if (holder.getPostChain() != null)
                holder.getPostChain().close();
    }

    @Inject(method = "resize", at = @At("TAIL"))
    public void resizePaCoPostShaders(int width, int height, CallbackInfo ci) {
        for (PostChainHolder holder : PaCoPostShaderRegistry.POST_SHADERS)
            if (holder.getPostChain() != null)
                holder.getPostChain().resize(width, height);
    }

    @Unique
    private void loadPaCoPostShaders(ResourceProvider resourceProvider) {
        for (PostChainHolder holder : PaCoPostShaderRegistry.POST_SHADERS) {
            // If the post shader is already loaded we close it
            if (holder.getPostChain() != null)
                holder.getPostChain().close();
            // We attempt to load the post shader
            try {
                Minecraft minecraft = Minecraft.getInstance();
                holder.setPostChain(new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getMainRenderTarget(), holder.getResourceLocation()));
                holder.getPostChain().resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            } catch (IOException iOException) {
                PandoraCore.LOGGER.warn("Failed to load shader: {}", holder.getResourceLocation(), iOException);
            } catch (JsonSyntaxException jsonSyntaxException) {
                PandoraCore.LOGGER.warn("Failed to parse shader: {}", holder.getResourceLocation(), jsonSyntaxException);
            }
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void applyPaCoCameraZRot(float partialTick, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(((IPaCoSetCameraRotation) this.mainCamera).pandoraCore$getZRot()));
    }
}