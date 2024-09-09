package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.github.andrew0030.pandora_core.client.shader.holder.PaCoUniformHolder;
import com.github.andrew0030.pandora_core.client.shader.holder.PostChainHolder;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.post.IPaCoUniformAccess;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public class PaCoPostShaders {

    /** See {@link PaCoScreen} for an example on how to use <strong>parameters</strong>, and or {@link PaCoUniformHolder}. */
    public static final PostChainHolder PACO_BLUR = PaCoPostShaderRegistry.register(
            new ResourceLocation(PandoraCore.MOD_ID, "shaders/post/blur.json"),
            (postChain, partialTick, parameters) -> {
                float radius = (float) parameters.getOrDefault("radius", 5.0F);
                if (postChain != null) {
                    RenderSystem.enableBlend();
                    ((IPaCoUniformAccess) postChain).pandoraCore$setUniform("Radius", radius);
                    postChain.process(partialTick);
                    RenderSystem.disableBlend();
                }
            }
    );

    public static final class BlurVariables {
        public static final PaCoUniformHolder RADIUS_MUL = PACO_BLUR.getUniform("RadiusMultiplier");
        public static final PaCoUniformHolder PASS0_MUL = RADIUS_MUL.tagged("pass_0");
        public static final PaCoUniformHolder PASS1_MUL = RADIUS_MUL.tagged("pass_1");
        public static final PaCoUniformHolder PASS2_MUL = RADIUS_MUL.tagged("pass_2");
    }

    public static void init() {}
}