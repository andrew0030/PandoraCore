package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import java.io.IOException;

public class ShaderAttachment {
    int id;
    AttachmentType type;

    public ShaderAttachment(
            String source, AttachmentType type,
            TemplateTransformation transformation,
            ShaderInstance vanilla, boolean processSource,
            VariableMapper mapper, TransformationProcessor processor,
            ResourceLocation location
    ) {
        this.type = type;

        if (processSource && processor != null && transformation != null) {
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
        GL20.glShaderSource(id, source.replace("out struct", "out").replace(";]", "]"));
//        GL20.glShaderSource(id, source);
        GL20.glCompileShader(id);

        if (GlStateManager.glGetShaderi(id, 35713) == 0) {
            String $$7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(id, 32768));
            GL20.glDeleteShader(id);
            try {
                throw new IOException("Couldn't compile " + location.toString() + " from " + vanilla.getName() + " program (" + vanilla.getName() + ", " + location + ") : " + $$7);
            } catch (Throwable err) {
                throw new RuntimeException(err);
            }
        }
    }

    public void delete() {
        GL20.glDeleteShader(id);
    }

    public int getId() {
        return id;
    }
}
