package com.github.andrew0030.pandora_core.mixin.post_shader;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.github.andrew0030.pandora_core.client.shader.holder.PostChainHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public class GameRenderer_PostShaderMixin {
    private static final @Unique Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "GameRenderer_PostShaderMixin");

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    public void initPaCoPostShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        this.pandoraCore$loadPostShaders(resourceProvider);
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
    private void pandoraCore$loadPostShaders(ResourceProvider resourceProvider) {
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
                LOGGER.warn("Failed to load post shader: {}", holder.getResourceLocation(), iOException);
            } catch (JsonSyntaxException jsonSyntaxException) {
                LOGGER.warn("Failed to parse post shader: {}", holder.getResourceLocation(), jsonSyntaxException);
            }
        }
    }
}