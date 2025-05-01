package com.github.andrew0030.pandora_core.client.shader.templating.wrapper;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.LoaderCapabilities;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.LoaderCapability;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ShaderWrapper {
    ResourceLocation location;
    Map<ShaderLoader, TemplatedShaderInstance> instances = new HashMap<>();

    // cache these two as they are the likely two to be used
    TemplatedShaderInstance UI_DRAW;
    TemplatedShaderInstance ALL_DRAW;

    public TemplatedShaderInstance unwrap(
            LoaderCapability... requestedCapabilities
    ) {
        if (requestedCapabilities.length == 1) {
            if (requestedCapabilities[0].equals(LoaderCapabilities.UI_DRAW)) {
                if (UI_DRAW == null) {
                    UI_DRAW = TemplateManager.choose(
                            requestedCapabilities, instances, location
                    );
                }

                return UI_DRAW;
            }
        }

        TemplateManager.choose(
                requestedCapabilities, instances, location
        );
    }
}
