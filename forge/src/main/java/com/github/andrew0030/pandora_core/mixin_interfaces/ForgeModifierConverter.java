package com.github.andrew0030.pandora_core.mixin_interfaces;

import net.minecraftforge.common.world.BiomeModifier;

@FunctionalInterface
public interface ForgeModifierConverter {
    BiomeModifier convertToForgeModifier();
}