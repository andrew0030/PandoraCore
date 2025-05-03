package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program;

import com.github.andrew0030.pandora_core.client.render.SupportChecker;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.mojang.blaze3d.shaders.AbstractUniform;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.INTELBlackholeRender;

import java.io.InputStream;

public class BlackHoleProgram extends BaseProgram {
    public static final BlackHoleProgram INSTANCE = new BlackHoleProgram();

    private final int id;

    public BlackHoleProgram() {
        ClassLoader ldr = BlackHoleProgram.class.getClassLoader();
        try {
            InputStream is = ldr.getResourceAsStream("data/blackhole.fsh");
            byte[] data = is.readAllBytes();
            String frag = new String(data);
            try {
                is.close();
            } catch (Throwable ignored) {
            }
            is = ldr.getResourceAsStream("data/blackhole.vsh");
            data = is.readAllBytes();
            String vert = new String(data);
            try {
                is.close();
            } catch (Throwable ignored) {
            }

            int f = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
            GL30.glShaderSource(f, frag);
            GL30.glCompileShader(f);
            int v = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
            GL30.glShaderSource(v, vert);
            GL30.glCompileShader(v);

            id = GL30.glCreateProgram();
            GL30.glAttachShader(id, f);
            GL30.glAttachShader(id, v);
            GL30.glValidateProgram(id);
            GL30.glLinkProgram(id);

            GL30.glDeleteShader(f);
            GL30.glDeleteShader(v);
        } catch (Throwable err) {
            throw new RuntimeException("wth");
        }
    }

    @Override
    public void bind() {
        if (SupportChecker.SUPPORT_BLACKHOLE) {
            GL30.glEnable(INTELBlackholeRender.GL_BLACKHOLE_RENDER_INTEL);
        }
        GL30.glUseProgram(id);
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void clear() {
        GL30.glUseProgram(0);
        if (SupportChecker.SUPPORT_BLACKHOLE) {
            GL30.glDisable(INTELBlackholeRender.GL_BLACKHOLE_RENDER_INTEL);
        }
    }

    @Override
    public void upload() {
        // no-op
    }

    @Override
    public AbstractUniform getUniform(String name, int type, int count) {
        return TemplatedShader.ABSTRACT_INST;
    }

    @Override
    public int getAttributeLocation(String name) {
        return -1;
    }
}
