package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.modules.templater.TemplateManager;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoShaderStateShard;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PaCoRenderTypes {
    public static final ShaderWrapper shader = TemplateManager.getWrapper(new ResourceLocation("pandora_core:shaders/paco/templated/entity_instanced"));
    public static final ShaderWrapper fail = TemplateManager.getWrapper(new ResourceLocation("pandora_core:shaders/paco/templated/fail"));

    public static final PaCoShaderStateShard shaderStateShard = new PaCoShaderStateShard(shader);

    public static final RenderType type = RenderType.create(
            "pandora_core:test",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.TRIANGLES,
            DefaultVertexFormat.NEW_ENTITY.getVertexSize() * 64,
            true, false,
            RenderType.CompositeState.builder()
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setShaderState(shaderStateShard)
                    .createCompositeState(true)
    );
}
