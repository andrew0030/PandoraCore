package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.registry.internal.PaCoModifiers;
import com.github.andrew0030.pandora_core.registry.internal.PaCoRegistryKeys;
import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Modifier {
    Codec<Modifier> CODEC = ExtraCodecs.lazyInitializedCodec(
        () -> PaCoModifiers.getRegistry().byNameCodec()
    ).dispatch(Modifier::codec, Function.identity());

    /** @return The {@link Codec} used to (de)serialize this {@link Modifier}. */
    Codec<? extends Modifier> codec();

    /** @return The {@link Phase} at which this {@link Modifier} should run. */
    Phase phase();

    /** Applies this {@link Modifier} (called before world load). */
    void applyModifier();

    /** Convenience method to apply all {@link Modifier Modifiers} in the dynamic registry. */
    static void applyAll(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        Map<Phase, List<Modifier>> toAdd = new EnumMap<>(Phase.class);

        // Gets all modifiers from the registry and caches them into "toAdd"
        registryAccess.lookupOrThrow(PaCoRegistryKeys.WORLDGEN_MODIFIER).listElements().forEach(modifierReference -> {
            Modifier modifier = modifierReference.value();
            if (modifier.phase() != Phase.NONE) // Skips NONE modifiers to save a tiny bit of overhead
                toAdd.computeIfAbsent(modifier.phase(), phase -> new ArrayList<>()).add(modifier);
        });

        // Loops over all phases and adds their corresponding modifiers
        for (Phase phase : Phase.PHASES) {
            // Skips modifiers with the NONE phase
            if (phase == Phase.NONE) continue;
            // Applies all modifiers matching the current phase
            List<Modifier> modifiers = toAdd.get(phase);
            if (modifiers != null) // In case there are no modifiers
                modifiers.forEach(Modifier::applyModifier);
        }
    }

    enum Phase {
        /** Everything that will never get applied. */
        NONE,
        /** Everything that needs to run before the standard phases. */
        BEFORE_EVERYTHING,
        /** Adding features, mob spawns, etc. */
        ADD,
        /** Removing features, mob spawns, etc. */
        REMOVE,
        /** Modifying single values (e.g., climate, colors). */
        MODIFY,
        /** Everything that needs to run after the standard phases. */
        AFTER_EVERYTHING;

        // A cache of all Phase values to avoid repeated calls to Enum#values()
        private static final Phase[] PHASES = Phase.values();
    }
}