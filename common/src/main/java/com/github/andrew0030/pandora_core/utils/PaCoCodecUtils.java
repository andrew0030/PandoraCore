package com.github.andrew0030.pandora_core.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.List;

public class PaCoCodecUtils {

    /**
     * Accepts either a {@code single} element or an {@code array} of
     * elements in JSON and always returns a {@link List} at runtime.
     */
    public static <E> MapCodec<List<E>> singleOrList(Codec<E> elementCodec, String fieldName) {
        return Codec.mapEither( // Tries to parse the first CODEC, and on failure tries the second one
                elementCodec.listOf().fieldOf(fieldName),
                elementCodec.fieldOf(fieldName)
        ).xmap( // Transforms the 'Either' into the desired runtime type (List)
                either -> either.map(list -> list, List::of), // Kept as is (List), or wrapped into a List
                Either::left                                  // Always encode as a List
        );
    }
}