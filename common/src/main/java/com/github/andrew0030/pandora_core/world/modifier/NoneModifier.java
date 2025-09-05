package com.github.andrew0030.pandora_core.world.modifier;

import com.mojang.serialization.Codec;

/**
 * The {@link NoneModifier} does nothing, it's intended for mod pack makers
 * and players to be used, to disable modifiers by overriding them.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:none"
 * }
 * }</pre>
 */
public record NoneModifier() implements Modifier {
    public static final Codec<NoneModifier> CODEC = Codec.unit(NoneModifier::new);

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public Phase phase() {
        return Phase.NONE;
    }

    @Override
    public void applyModifier() {}
}