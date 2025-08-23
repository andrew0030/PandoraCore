package com.github.andrew0030.pandora_core.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

//TODO maybe replace LinkedHashSet with just a HashSet for single entries, might be
// slightly better for memory, and since its a single element ordering shouldn't matter
public class PaCoCodecUtils {

    /**
     * Returns a {@link Codec} that accepts either a single {@code element} or an {@code array}
     * and maps to a {@link List} at runtime.<br/>
     * When encoding, preserves short-form for singletons.
     *
     * <p>Usage:</p>
     * <pre>{@code
     * PaCoCodecUtils.singleOrList(Example.CODEC)
     *     .fieldOf("name")
     *     .forGetter(...);
     * </pre>
     */
    public static <E> Codec<List<E>> singleOrList(Codec<E> elementCodec) {
        return new EitherCodec<>( // Tries to parse the first CODEC, and on failure tries the second one
            elementCodec.listOf(),
            elementCodec
        ).xmap( // Transforms the 'Either' into the desired runtime type (List)
            either -> either.map(list -> list, List::of), // Kept as is (List), or wrapped into a List
            // Encodes as a single element if the List contains only one entry. Otherwise, encodes as an array
            list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list)
        );
    }

    /**
     * Returns a {@link Codec} that accepts either a single {@code element} or an {@code array}
     * and maps to a {@link List} at runtime.<br/>
     * When encoding, it always encodes as an array.
     *
     * <p>Usage:</p>
     * <pre>{@code
     * PaCoCodecUtils.singleOrListForceArray(Example.CODEC)
     *     .fieldOf("name")
     *     .forGetter(...);
     * </pre>
     */
    public static <E> Codec<List<E>> singleOrListForceArray(Codec<E> elementCodec) {
        return new EitherCodec<>( // Tries to parse the first CODEC, and on failure tries the second one
                elementCodec.listOf(),
                elementCodec
        ).xmap( // Transforms the 'Either' into the desired runtime type (List)
                either -> either.map(list -> list, List::of), // Kept as is (List), or wrapped into a List
                Either::left                                  // Always encodes as an array
        );
    }

    /**
     * Returns a {@link Codec} that accepts either a single {@code element} or an {@code array}
     * and maps to a {@link Set} at runtime.<br/>
     * When encoding, preserves short-form for singletons.
     *
     * <p>Usage:</p>
     * <pre>{@code
     * PaCoCodecUtils.singleOrSet(Example.CODEC)
     *     .fieldOf("name")
     *     .forGetter(...);
     * </pre>
     */
    public static <E> Codec<Set<E>> singleOrSet(Codec<E> elementCodec) {
        // Tries to parse the first CODEC, and on failure tries the second one
        return new EitherCodec<>(
            elementCodec.listOf(),
            elementCodec
        ).xmap( // Transforms the 'Either' into the desired runtime type (Set)
            either -> either.map(
                list -> new LinkedHashSet<>(list), // If a list is found we create a Set with it
                single -> {                        // If a single element is found, it gets wrapped into a Set
                    LinkedHashSet<E> s = new LinkedHashSet<>();
                    s.add(single);
                    return s;
                }),
            set -> { // Encodes as a single element if the Set contains only one entry. Otherwise, encodes as an array
                List<E> list = new ArrayList<>(set);
                return list.size() == 1 ? Either.right(list.get(0)) : Either.left(list);
            }
        );
    }

    /**
     * Returns a {@link Codec} that accepts either a single {@code element} or an {@code array}
     * and maps to a {@link Set} at runtime.<br/>
     * When encoding, it always encodes as an array.
     *
     * <p>Usage:</p>
     * <pre>{@code
     * PaCoCodecUtils.singleOrSetForceArray(Example.CODEC)
     *     .fieldOf("name")
     *     .forGetter(...);
     * </pre>
     */
    public static <E> Codec<Set<E>> singleOrSetForceArray(Codec<E> elementCodec) {
        // Tries to parse the first CODEC, and on failure tries the second one
        return new EitherCodec<>(
            elementCodec.listOf(),
            elementCodec
        ).xmap( // Transforms the 'Either' into the desired runtime type (Set)
            either -> either.map(
                list -> new LinkedHashSet<>(list), // If a list is found we create a Set with it
                single -> {                        // If a single element is found, it gets wrapped into a Set
                    LinkedHashSet<E> s = new LinkedHashSet<>();
                    s.add(single);
                    return s;
                }),
            set -> Either.left(new ArrayList<>(set)) // Always encodes as an array
        );
    }
}