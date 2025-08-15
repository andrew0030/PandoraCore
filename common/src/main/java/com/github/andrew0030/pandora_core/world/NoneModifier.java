package com.github.andrew0030.pandora_core.world;

import com.mojang.serialization.Codec;

//TODO write javadocs
public record NoneModifier() implements Modifier {
    public static final Codec<NoneModifier> CODEC = Codec.unit(NoneModifier::new);

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public void applyModifier() {}
}