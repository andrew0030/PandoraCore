package com.github.andrew0030.pandora_core.client.render;

import com.github.andrew0030.pandora_core.platform.Services;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;

public class BufferBuilderUtils {
    /**
     * Enforces a buffer builder to be started with extended vertex format if iris is present and a shader is enabled
     * this is done for shader compatibility
     *
     * @param builder the builder to start building
     * @param mode    the polygon type
     * @param format  the format of the vertices
     * @return the buffer builder
     */
    public static BufferBuilder enforceExtended(BufferBuilder builder, VertexFormat.Mode mode, VertexFormat format) {
        if (Services.PLATFORM.isModLoaded("iris")) {
            boolean usingExtended = WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat();
            WorldRenderingSettings.INSTANCE.setUseExtendedVertexFormat(true);
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            WorldRenderingSettings.INSTANCE.setUseExtendedVertexFormat(usingExtended);
        } else {
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        }
        return builder;
    }
}
