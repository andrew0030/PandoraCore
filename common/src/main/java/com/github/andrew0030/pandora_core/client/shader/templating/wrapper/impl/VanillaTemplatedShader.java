package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoAccessibleProgram;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoConditionallyBindable;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;

public class VanillaTemplatedShader extends TemplatedShader {
    ShaderInstance vanilla;
    int id;
    String vshName, fshName;

    public VanillaTemplatedShader(
            TemplateLoader loader, TemplateTransformation transformation,
            String template,
            ShaderInstance vanilla,
            String vsh, String fsh,
            String vshName, String fshName
    ) {
        super(loader, transformation, template);
        this.vanilla = vanilla;

        this.vshName = vshName + ".vsh";
        this.fshName = fshName + ".fsh";

        id = GL20.glCreateProgram();
        // load transformed vertex shader
        int vertId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        {
            GL20.glShaderSource(vertId, vsh);
            GL20.glCompileShader(vertId);
            if (GlStateManager.glGetShaderi(vertId, 35713) == 0) {
                String $$7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(vertId, 32768));
                GL20.glDeleteShader(vertId);
                GL20.glDeleteProgram(id);
                try {
                    throw new IOException("Couldn't compile " + transformation.location + " from " + vanilla.getName() + " program (" + vanilla.getName() + ", " + transformation.location + ") : " + $$7);
                } catch (Throwable err) {
                    throw new RuntimeException(err);
                }
            }
        }

        int fragId = ((IPaCoAccessibleProgram) vanilla.getFragmentProgram()).pandoraCore$getId();
        // bind shaders
        GL20.glAttachShader(id, vertId);
        GL20.glAttachShader(id, fragId);
        GL20.glLinkProgram(id);
        // delete vertex shader as it is now bound and not necessary
        GL20.glDeleteShader(vertId);
    }

    @Override
    public void apply() {
        RenderSystem.setShader(() -> vanilla);
        ((IPaCoConditionallyBindable) vanilla).pandoraCore$disableBind();
        GL20.glUseProgram(id);
    }

    @Override
    public void upload() {
        vanilla.apply();
    }

    @Override
    public void destroy() {
        GL20.glDeleteProgram(id);
    }

    @Override
    public void clear() {
        ((IPaCoConditionallyBindable) vanilla).pandoraCore$enableBind();
        super.clear();
    }

    @Override
    public boolean matches(String mod, String active) {
        return vshName.equals(mod + ":" + active) || fshName.equals(mod + ":" + active);
    }


}
