package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.registry.PaCoBlockRenderTypeRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoBlocks;
import net.minecraft.client.renderer.RenderType;

public class PaCoBlockRenderTypes {
    public static final PaCoBlockRenderTypeRegistry RENDER_TYPES = new PaCoBlockRenderTypeRegistry();

    static {
        RENDER_TYPES.add(RenderType.cutoutMipped(), PaCoBlocks.FOLIAGE_TEST);
    }
}