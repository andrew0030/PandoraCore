package com.github.andrew0030.pandora_core.client.shader.templating.wrapper;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapability;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ShaderWrapper {
    ResourceLocation location;
    Map<ShaderLoader, TemplatedShaderInstance> instances = new HashMap<>();

    public ShaderWrapper(ResourceLocation location) {
        this.location = location;
    }

    // cache these two as they are the likely two to be used
    TemplatedShaderInstance UI_DRAW;
    TemplatedShaderInstance ALL_DRAW;

    public TemplatedShaderInstance unwrap(
            ShaderCapability... requestedCapabilities
    ) {
        return null;
//        if (requestedCapabilities.length == 1) {
//            if (requestedCapabilities[0].equals(ShaderCapabilities.UI_DRAW)) {
//                if (UI_DRAW == null) {
//                    UI_DRAW = TemplateManager.choose(
//                            requestedCapabilities, instances, location
//                    );
//                }
//
//                return UI_DRAW;
//            }
//        }
//
//        TemplateManager.choose(
//                requestedCapabilities, instances, location
//        );
    }

    public void apply() {
        // TODO
    }

    public void upload() {
        // TODO
    }

    public void clear() {
        // TODO
    }
}
