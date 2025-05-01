package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.PandoraCore;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;

@SuppressWarnings("unused")
public class ShaderCapabilities {
    public static final ShaderCapability UI_DRAW = new ShaderCapability(new ResourceLocation(PandoraCore.MOD_ID, "ui_draw"), "UI");
    public static final ShaderCapability WORLD_DRAW = new ShaderCapability(new ResourceLocation(PandoraCore.MOD_ID, "world_draw"), "WORLD");
    public static final ShaderCapability SHADOW_DRAW = new ShaderCapability(new ResourceLocation(PandoraCore.MOD_ID, "shadow_draw"), "SHADOW");

    public static final ShaderCapabilities CAPABILITIES_ALL_VANILLA = new ShaderCapabilities(UI_DRAW, WORLD_DRAW);
    public static final ShaderCapabilities CAPABILITIES_WORLD_SHADOW = new ShaderCapabilities(WORLD_DRAW, SHADOW_DRAW);
    public static final ShaderCapabilities CAPABILITIES_WORLD_ONLY = new ShaderCapabilities(WORLD_DRAW);
    public static final ShaderCapabilities CAPABILITIES_UI_ONLY = new ShaderCapabilities(UI_DRAW);

    private final ShaderCapability[] capabilities;
    private final HashSet<ShaderCapability> capabilitiesSet;

    public ShaderCapabilities(ShaderCapability... capabilities) {
        this.capabilities = capabilities;
        this.capabilitiesSet = new HashSet<>(Arrays.asList(capabilities));
    }

    public boolean hasCapability(ShaderCapability capability) {
        return this.capabilitiesSet.contains(capability);
    }

    public boolean supports(ShaderCapability[] requestedCapabilities) {
        for (ShaderCapability requestedCapability : requestedCapabilities) {
            if (!hasCapability(requestedCapability)) return false;
        }
        return true;
    }

    public String debugString() {
        StringBuilder builder = new StringBuilder();
        for (ShaderCapability capability : capabilities) {
            builder.append(capability.debugString).append("_");
        }
        return builder.toString().substring(0, builder.length() - 1);
    }
}
