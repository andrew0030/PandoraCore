package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.registry.PaCoColorHandlerRegistry;
import com.github.andrew0030.pandora_core.registry.test.PaCoBlocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;

public class PaCoColorHandlers {
    public static final PaCoColorHandlerRegistry COLOR_HANDLERS = new PaCoColorHandlerRegistry();

    static {
        // Blocks
        COLOR_HANDLERS.addBlock(
            (state, level, pos, tintIndex) -> (level != null && pos != null) ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
            PaCoBlocks.FOLIAGE_TEST
        );
        // Items
        COLOR_HANDLERS.addItem(
            (stack, tintIndex) -> FoliageColor.getDefaultColor(),
            PaCoBlocks.FOLIAGE_TEST
        );
    }
}