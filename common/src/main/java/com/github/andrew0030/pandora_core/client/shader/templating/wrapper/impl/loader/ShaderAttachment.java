package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.NameMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ShaderInstance;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;

public class ShaderAttachment {
    int id;

    public ShaderAttachment(
            String source, AttachmentType type,
            TemplateTransformation transformation,
            ShaderInstance vanilla, boolean processSource,
            VariableMapper mapper, TransformationProcessor processor
    ) {
        if (processSource) {
            source = processor.process(mapper, source, transformation);
        }

        id = GL20.glCreateShader(
                switch (type) {
                    case VERTEX -> GL20.GL_VERTEX_SHADER;
                    case FRAGMENT -> GL20.GL_FRAGMENT_SHADER;
                    case GEOMETRY -> GL32.GL_GEOMETRY_SHADER;
                    case TESSELATION_EVAL -> GL40.GL_TESS_EVALUATION_SHADER;
                    case TESSELATION_CONTROL -> GL40.GL_TESS_CONTROL_SHADER;
                }
        );
        GL20.glShaderSource(id, source);

        if (GlStateManager.glGetShaderi(id, 35713) == 0) {
            String $$7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(id, 32768));
            GL20.glDeleteShader(id);
            GL20.glDeleteProgram(id);
            try {
                throw new IOException("Couldn't compile " + transformation.location + " from " + vanilla.getName() + " program (" + vanilla.getName() + ", " + transformation.location + ") : " + $$7);
            } catch (Throwable err) {
                throw new RuntimeException(err);
            }
        }
    }
}
