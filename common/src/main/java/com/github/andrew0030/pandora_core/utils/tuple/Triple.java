package com.github.andrew0030.pandora_core.utils.tuple;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * A simple <b>immutable</b> tuple of three values.
 *
 * <p>Usage:
 * <pre><code>
 *   Triple<String, Integer, Double> triple = Triple.of("foo", 1, 2.0);
 *   String s = t.getFirst();
 *   Triple<Integer, String, Double> swapped = triple.swapFirstAndSecond();
 * </code></pre>
 *
 * This class follows the same {@code App} / {@code Mu} pattern as Mojang's {@link Pair}, so it can be used
 * with the same functional type-classes that accept {@code App<Triple.Mu<S,T>, F>}.
 *
 * @param <F> The first component's type
 * @param <S> The second component's type
 * @param <T> The third component's type
 */
public class Triple<F, S, T> implements App<Triple.Mu<S, T>, F> {
    private final F first;
    private final S second;
    private final T third;

    /** Marker type used to treat {@link Triple} as a unary type constructor (fixing {@code second} and {@code third}). */
    public static final class Mu<S, T> implements K1 {}

    /**
     * Used to safely cast an {@link App} back to a {@link Triple}.
     *
     * @param box The boxed triple (as {@link App})
     * @param <F> The first component type
     * @param <S> The second component type
     * @param <T> The third component type
     * @return The unboxed {@link Triple}
     */
    public static <F, S, T> Triple<F, S, T> unbox(final App<Mu<S, T>, F> box) {
        return (Triple<F, S, T>) box;
    }

    /**
     * The {@link Triple} constructor.
     *
     * @param first  The first component
     * @param second The second component
     * @param third  The third component
     */
    public Triple(final F first, final S second, final T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /** @return The first component */
    public F getFirst() {
        return this.first;
    }

    /** @return The second component */
    public S getSecond() {
        return this.second;
    }

    /** @return The third component */
    public T getThird() {
        return this.third;
    }

    /**
     * Used to create a {@link Triple}.
     *
     * @param first  The first component
     * @param second The second component
     * @param third  The third component
     */
    public static <F, S, T> Triple<F, S, T> of(final F first, final S second, final T third) {
        return new Triple<>(first, second, third);
    }

    /** Maps the {@code first} component, leaving {@code second} and {@code third} untouched. */
    public <F2> Triple<F2, S, T> mapFirst(final Function<? super F, ? extends F2> function) {
        return Triple.of(function.apply(first), second, third);
    }

    /** Maps the {@code second} component, leaving {@code first} and {@code third} untouched. */
    public <S2> Triple<F, S2, T> mapSecond(final Function<? super S, ? extends S2> function) {
        return Triple.of(first, function.apply(second), third);
    }

    /** Maps the {@code third} component, leaving {@code first} and {@code second} untouched. */
    public <T2> Triple<F, S, T2> mapThird(final Function<? super T, ? extends T2> function) {
        return Triple.of(first, second, function.apply(third));
    }

    /**
     * Swaps {@code first} and {@code second} components.
     *
     * @return A {@link Triple} where {@code first}&{@code second} are swapped: (second, first, third)
     */
    public Triple<S, F, T> swapFirstAndSecond() {
        return Triple.of(second, first, third);
    }

    /**
     * Swaps {@code first} and {@code third} components.
     *
     * @return A {@link Triple} where {@code first}&{@code third} are swapped: (third, second, first)
     */
    public Triple<T, S, F> swapFirstAndThird() {
        return Triple.of(third, second, first);
    }

    /**
     * Swaps {@code second} and {@code third} components.
     *
     * @return A {@link Triple} where {@code second}&{@code third} are swapped: (first, third, second)
     */
    public Triple<F, T, S> swapSecondAndThird() {
        return Triple.of(first, third, second);
    }

    /**
     * A simple {@link Collector} that collects {@link Triple Triples} into a {@link Map} keyed by their {@code first} element
     * with a {@link Pair} of ({@code second}, {@code third}) as the value.
     *
     * <p>Usage: {@code streamOfTriples.collect(Triple.toMapWithPairValue());}
     */
    public static <F, S, T> Collector<Triple<F, S, T>, ?, Map<F, Pair<S, T>>> toMapWithPairValue() {
        return Collectors.toMap(Triple::getFirst, triple -> Pair.of(triple.getSecond(), triple.getThird()));
    }

    @Override
    public String toString() {
        return "(" + this.first + ", " + this.second + ", " + this.third + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Triple<?, ?, ?> other)) return false;
        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second) && Objects.equals(this.third, other.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third);
    }
}