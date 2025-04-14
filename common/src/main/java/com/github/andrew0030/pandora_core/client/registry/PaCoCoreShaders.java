package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

public class PaCoCoreShaders implements PacoResourceManager {

    // TODO think of a way to abstract this.
    @Nullable
    public static ShaderInstance positionColorTexFullAlphaShader;

    public static ShaderInstance getPositionColorTexFullAlphaShader() {
        return Objects.requireNonNull(PaCoCoreShaders.positionColorTexFullAlphaShader, "Attempted to call getPositionColorTexFullAlphaShader before shaders have finished loading.");
    }

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        dispatcher.apply("paco_core_shader_loading", () -> {
            if (PaCoCoreShaders.positionColorTexFullAlphaShader != null)
                PaCoCoreShaders.positionColorTexFullAlphaShader.close();
            try {
                PaCoCoreShaders.positionColorTexFullAlphaShader = new ShaderInstance(manager, PandoraCore.MOD_ID + ":position_color_tex_full_alpha", DefaultVertexFormat.POSITION_COLOR_TEX);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}