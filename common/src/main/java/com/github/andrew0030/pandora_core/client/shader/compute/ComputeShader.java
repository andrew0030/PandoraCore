package com.github.andrew0030.pandora_core.client.shader.compute;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import org.slf4j.Logger;

import java.io.IOException;

public class ComputeShader {
    int id;

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Compute Shaders", "Linker");

    public ComputeShader(ResourceLocation location, String src) {
        int code = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);

        // TODO: import processor
        // TODO: OpenCL?
        GL20.glShaderSource(code, src);
        GL20.glCompileShader(code);

        if (GlStateManager.glGetShaderi(code, 35713) == 0) {
            String $$7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(code, 32768));
            GL20.glDeleteShader(code);
            try {
                throw new IOException("Couldn't compile " + location.toString() + ": " + $$7);
            } catch (Throwable err) {
                throw new RuntimeException(err);
            }
        }

        id = GL20.glCreateProgram();
        GL20.glAttachShader(id, code);
        GL20.glValidateProgram(id);
        GL20.glLinkProgram(id);

        int i = GlStateManager.glGetProgrami(id, 35714);
        if (i == 0) {
            String $$7 = StringUtils.trim(GlStateManager.glGetProgramInfoLog(code, 32768));
            try {
                throw new IOException("Error encountered when linking program " + location.toString() + ": " + $$7);
            } catch (Throwable err) {
                throw new RuntimeException(err);
            }
        }

        GL20.glDeleteShader(code);
    }
}
