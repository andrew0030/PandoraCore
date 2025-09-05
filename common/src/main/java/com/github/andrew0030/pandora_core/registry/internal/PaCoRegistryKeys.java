package com.github.andrew0030.pandora_core.registry.internal;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.world.modifier.Modifier;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PaCoRegistryKeys {
    public static final ResourceKey<Registry<Modifier>> WORLDGEN_MODIFIER = ResourceKey.createRegistryKey(new ResourceLocation(PandoraCore.MOD_ID, "worldgen_modifier"));
    public static final ResourceKey<Registry<Codec<? extends Modifier>>> MODIFIER_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(PandoraCore.MOD_ID, "modifier_type"));
}