package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.registry.internal.PaCoModifiers;
import com.github.andrew0030.pandora_core.registry.internal.PaCoRegistryKeys;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface Modifier {
    Codec<Modifier> CODEC = ExtraCodecs.lazyInitializedCodec(
            () -> PaCoModifiers.getRegistry().byNameCodec()
    ).dispatch(Modifier::codec, Function.identity());

    // TODO: Maybe add phases like forge biome modifiers? Might be handy to ensure removal happens after additions...

    /** @return The {@link Codec} used to (de)serialize this {@link Modifier}. */
    Codec<? extends Modifier> codec();

    /** Applies this {@link Modifier} (called before world load). */
    void applyModifier();

    /** Convenience method to apply all {@link Modifier Modifiers} in the dynamic registry. */
    static void applyAll(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        HolderLookup.RegistryLookup<Modifier> modifiers = registryAccess.lookupOrThrow(PaCoRegistryKeys.WORLDGEN_MODIFIER);
        modifiers.listElements().forEach(entry -> entry.value().applyModifier());
    }
}