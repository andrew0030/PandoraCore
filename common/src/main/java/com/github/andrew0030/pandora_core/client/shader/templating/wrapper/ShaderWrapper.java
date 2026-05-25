package com.github.andrew0030.pandora_core.client.shader.templating.wrapper;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapabilities;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapability;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.TemplatedProgram;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ShaderWrapper {
    ResourceLocation location;
    Map<ShaderLoader, TemplatedShader> instances = new HashMap<>();

    public ShaderWrapper(ResourceLocation location) {
        this.location = location;
    }

    // cache these two as they are the likely two to be used
    TemplatedShader UI_DRAW;
    TemplatedShader WORLD_DRAW;
    TemplatedShader WORLD_SHADOW_DRAW;

    TemplatedShader activeUnwrap;

	// TODO: loader priority based on paco render state
	// in GUI->vanilla loader
	// in world->iris/optifine if enabled, elsewise vanilla
	// elsewise, other mods can manage render state themselves if they need to
	// render state should define required capabilities and loader priority, both nullable
	// by default, choose highest loader that matches required capabilities, or highest loader if no capabilities are requested
    public TemplatedShader unwrap(
            ShaderCapability... requestedCapabilities
    ) {
        if (requestedCapabilities.length == 1) {
            if (requestedCapabilities[0].equals(ShaderCapabilities.UI_DRAW)) {
                if (UI_DRAW == null) {
                    UI_DRAW = TemplateManager.choose(
                            requestedCapabilities, instances, location
                    );
                }

                return UI_DRAW;
            } else if (requestedCapabilities[0].equals(ShaderCapabilities.WORLD_DRAW)) {
                if (WORLD_DRAW == null) {
                    WORLD_DRAW = TemplateManager.choose(
                            requestedCapabilities, instances, location
                    );
                }

                return WORLD_DRAW;
            }
        } else if (requestedCapabilities.length == 2) {
            if (
                    (requestedCapabilities[0].equals(ShaderCapabilities.WORLD_DRAW) &&
                            requestedCapabilities[1].equals(ShaderCapabilities.SHADOW_DRAW)) ||
                            (requestedCapabilities[0].equals(ShaderCapabilities.SHADOW_DRAW) &&
                                    requestedCapabilities[1].equals(ShaderCapabilities.WORLD_DRAW))
            ) {
                if (WORLD_SHADOW_DRAW == null) {
                    WORLD_SHADOW_DRAW = TemplateManager.choose(
                            requestedCapabilities, instances, location
                    );
                }

                return WORLD_SHADOW_DRAW;
            }
        }

        return TemplateManager.choose(
                requestedCapabilities, instances, location
        );
    }

    public void apply() {
        activeUnwrap = unwrap();
        activeUnwrap.apply();
    }

    public void upload() {
        if (activeUnwrap == null)
            throw new RuntimeException("Must apply before uploading uniforms");
        activeUnwrap.upload();
    }

    public void clear() {
        if (activeUnwrap != null) activeUnwrap.clear();
    }

    public void clearCache() {
        instances.clear();
        UI_DRAW = null;
        WORLD_DRAW = null;
        WORLD_SHADOW_DRAW = null;
    }

    public int getAttributeLocation(String name) {
        return activeUnwrap.getAttributeLocation(name);
    }
	
	public TemplatedShader getActiveUnwrap() {
		return activeUnwrap;
	}
}
