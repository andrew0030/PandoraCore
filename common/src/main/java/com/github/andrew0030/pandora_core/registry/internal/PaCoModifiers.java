package com.github.andrew0030.pandora_core.registry.internal;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.registry.PaCoRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryBuilder;
import com.github.andrew0030.pandora_core.world.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

public class PaCoModifiers {
    public static final PaCoRegistry<Codec<? extends Modifier>> MODIFIER_TYPES = new PaCoRegistry<>(PaCoRegistryBuilder.simple(PaCoRegistryKeys.MODIFIER_TYPE), PandoraCore.MOD_ID);

    public static final Supplier<Codec<? extends Modifier>> NONE            = MODIFIER_TYPES.add("none", () -> NoneModifier.CODEC);
    public static final Supplier<Codec<? extends Modifier>> ADD_FEATURES    = MODIFIER_TYPES.add("add_features", () -> AddFeaturesModifier.CODEC);
    public static final Supplier<Codec<? extends Modifier>> REMOVE_FEATURES = MODIFIER_TYPES.add("remove_features", () -> RemoveFeaturesModifier.CODEC);
    public static final Supplier<Codec<? extends Modifier>> ADD_SPAWNS      = MODIFIER_TYPES.add("add_spawns", () -> AddSpawnsModifier.CODEC);
    public static final Supplier<Codec<? extends Modifier>> REMOVE_SPAWNS   = MODIFIER_TYPES.add("remove_spawns", () -> RemoveSpawnsModifier.CODEC);

    @SuppressWarnings("unchecked")
    public static Registry<Codec<? extends Modifier>> getRegistry() {
        Registry<Codec<? extends Modifier>> registry = (Registry<Codec<? extends Modifier>>) BuiltInRegistries.REGISTRY.get(PaCoRegistryKeys.MODIFIER_TYPE.location());
        if (registry == null)
            throw new NullPointerException("Attempted to get 'MODIFIER_TYPES' registry, before it was registered!");
        return registry;
    }
}