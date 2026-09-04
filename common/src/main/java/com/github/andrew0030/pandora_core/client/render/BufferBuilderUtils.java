package com.github.andrew0030.pandora_core.client.render;

import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
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
        if (
                Services.PLATFORM.isModLoaded("iris") ||
                Services.PLATFORM.isModLoaded("oculus")
        ) {
            boolean usingExtended = WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat();
            WorldRenderingSettings.INSTANCE.setUseExtendedVertexFormat(true);
			// TODO: is there a reason I enforce NEW_ENTITY?
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            WorldRenderingSettings.INSTANCE.setUseExtendedVertexFormat(usingExtended);
        } else {
			if (OptifineAccessor.optifinePresent && ShaderChecker.isShaderActive()) {
				OptifineAccessor.prepareSVB(builder, VertexFormat.Mode.TRIANGLES, format);
				builder.begin(VertexFormat.Mode.TRIANGLES, format);
				OptifineAccessor.debug(builder);
			} else {
				builder.begin(VertexFormat.Mode.TRIANGLES, format);
			}
        }
        return builder;
    }
	
	private static final ThreadLocal<Boolean> wasItemRendering = new ThreadLocal<>();
	
	public static void beginExtendedRendering() {
		if (OptifineAccessor.optifinePresent) {
			wasItemRendering.set(
					OptifineAccessor.isItemRendering()
			);
			OptifineAccessor.setItemRendering(true);
		}
	}
	
	public static void endExtendedRendering() {
		if (OptifineAccessor.optifinePresent) {
			OptifineAccessor.setItemRendering(wasItemRendering.get());
			wasItemRendering.remove();
		}
	}
}
