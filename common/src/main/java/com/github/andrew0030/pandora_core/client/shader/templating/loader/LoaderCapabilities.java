package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.PandoraCore;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;

@SuppressWarnings("unused")
public class LoaderCapabilities {
    public static final LoaderCapability UI_DRAW = new LoaderCapability(new ResourceLocation(PandoraCore.MOD_ID, "ui_draw"));
    public static final LoaderCapability WORLD_DRAW = new LoaderCapability(new ResourceLocation(PandoraCore.MOD_ID, "world_draw"));
    public static final LoaderCapability SHADOW_DRAW = new LoaderCapability(new ResourceLocation(PandoraCore.MOD_ID, "shadow_draw"));

    public static final LoaderCapabilities CAPABILITIES_ALL_VANILLA = new LoaderCapabilities(UI_DRAW, WORLD_DRAW);
    public static final LoaderCapabilities CAPABILITIES_WORLD_SHADOW = new LoaderCapabilities(WORLD_DRAW, SHADOW_DRAW);
    public static final LoaderCapabilities CAPABILITIES_WORLD_ONLY = new LoaderCapabilities(WORLD_DRAW);
    public static final LoaderCapabilities CAPABILITIES_UI_ONLY = new LoaderCapabilities(UI_DRAW);

//    private final LoaderCapability[] capabilities;
    private final HashSet<LoaderCapability> capabilitiesSet;

    public LoaderCapabilities(LoaderCapability... capabilities) {
//        this.capabilities = capabilities;
        this.capabilitiesSet = new HashSet<>(Arrays.asList(capabilities));
    }

    public boolean hasCapability(LoaderCapability capability) {
        return this.capabilitiesSet.contains(capability);
    }
}
