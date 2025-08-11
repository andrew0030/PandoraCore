package com.github.andrew0030.pandora_core.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;


public class PaCoRegistryBuilder {

    /**
     * Creates a new registry specification for the given {@code resourceKey}.
     *
     * <p>The returned {@link SimpleSpec} can be used fluently to configure optional behaviour
     * such as network synchronization via {@link SimpleSpec#sync()}.</p>
     *
     * @param resourceKey The {@link ResourceKey} identifying the registry to create
     * @return An immutable {@link SimpleSpec} describing the registry, which can be used for chaining
     */
    public static <T> SimpleSpec<T> simple(ResourceKey<Registry<T>> resourceKey) {
        return new SimpleSpec<>(resourceKey, false, false, null);
    }

    /**
     * Creates a new dynamic registry specification for the given {@code resourceKey} and {@code codec}.
     *
     * <p>The entries of the registry will be loaded from data packs at the file path
     * {@code data/<entry namespace>/<registry namespace>/<registry path>/<entry path>.json}.
     *
     * <p>The returned {@link DynamicSpec} can be used fluently to configure optional behaviour
     * such as network synchronization via {@link DynamicSpec#sync(Codec)}.</p>
     *
     * @param resourceKey The {@link ResourceKey} identifying the registry to create
     * @param codec       The {@link Codec} used to serialize/deserialize entries in the registry
     * @return An immutable {@link DynamicSpec} describing the registry, which can be used for chaining
     */
    public static <T> DynamicSpec<T> dynamic(ResourceKey<Registry<T>> resourceKey, Codec<T> codec) {
        return new DynamicSpec<>(resourceKey, codec, null);
    }

    /**
     * Immutable descriptor for a registry.
     *
     * <p>Obtained by calling {@link PaCoRegistryBuilder#simple(ResourceKey)}.</p>
     *
     * @apiNote Consumers should only interact with the fluent construction surface.<br/>
     * The {@code get*} accessors are exposed only for the registration code!
     */
    public static final class SimpleSpec<T> {
        private final ResourceKey<Registry<T>> resourceKey;
        private final boolean sync;
        private final boolean persistent;
        private final ResourceLocation defaultId;

        private SimpleSpec(ResourceKey<Registry<T>> resourceKey, boolean sync, boolean persistent, @Nullable ResourceLocation defaultId) {
            this.resourceKey = Objects.requireNonNull(resourceKey);
            this.sync = sync;
            this.persistent = persistent;
            this.defaultId = defaultId;
        }

        /**
         * Causes the registry to synchronize.
         *
         * @return A new {@link SimpleSpec} with sync enabled
         */
        public SimpleSpec<T> sync() {
            return new SimpleSpec<>(this.resourceKey, true, this.persistent, null);
        }

        /**
         * Causes the registry to save.
         *
         * @return A new {@link SimpleSpec} with saving enabled
         * @apiNote <strong>This method hasn't been tested!</strong><br/>
         * It adds the {@code PERSISTED} attribute on {@code fabric}, and enables saving on {@code forge}.
         */
        public SimpleSpec<T> persistent() {
            return new SimpleSpec<>(this.resourceKey, this.sync, true, null);
        }

        /**
         * Causes the registry to have a default entry.
         *
         * @param defaultId The {@link ResourceLocation} of the default entry
         * @return A new {@link SimpleSpec} with the given {@code defaultId} as its default entry.
         */
        public SimpleSpec<T> defaulted(ResourceLocation defaultId) {
            return new SimpleSpec<>(this.resourceKey, this.sync, this.persistent, defaultId);
        }

        // -----------------------
        // Spec Getters (internal)
        // -----------------------
        /** <strong>INTERNAL</strong>: Returns the {@link ResourceKey} for the registry. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public ResourceKey<Registry<T>> getResourceKey() { return this.resourceKey; }
        /** <strong>INTERNAL</strong>: Returns whether to sync the registry. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public boolean getSync() { return this.sync; }
        /** <strong>INTERNAL</strong>: Returns whether to save the registry. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public boolean getPersistent() { return this.persistent; }
        /** <strong>INTERNAL</strong>: Returns an {@link Optional} containing the {@link ResourceLocation} of the default entry. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public Optional<ResourceLocation> getDefaultId() { return Optional.ofNullable(this.defaultId); }
    }

    /**
     * Immutable descriptor for a dynamic registry.
     *
     * <p>Obtained by calling {@link PaCoRegistryBuilder#dynamic(ResourceKey, Codec)}.</p>
     *
     * @apiNote Consumers should only interact with the fluent construction surface.<br/>
     * The {@code get*} accessors are exposed only for the registration code!
     */
    public static final class DynamicSpec<T> {
        private final ResourceKey<Registry<T>> resourceKey;
        private final Codec<T> codec;
        private final Codec<T> netCodec;

        private DynamicSpec(ResourceKey<Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> netCodec) {
            this.resourceKey = Objects.requireNonNull(resourceKey);
            this.codec = Objects.requireNonNull(codec);
            this.netCodec = netCodec;
        }

        /**
         * Causes the dynamic registry to synchronize, using the given {@code netCodec}.
         *
         * @param netCodec The network {@link Codec} of the registry contents.
         *                 Can be a reduced variant of the normal {@link Codec}
         *                 that omits data that is not needed on the client.
         * @return A new {@link DynamicSpec} with the provided network codec set
         */
        public DynamicSpec<T> sync(Codec<T> netCodec) {
            return new DynamicSpec<>(this.resourceKey, this.codec, netCodec);
        }

        // -----------------------
        // Spec Getters (internal)
        // -----------------------
        /** <strong>INTERNAL</strong>: Returns the {@link ResourceKey} for the registry. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public ResourceKey<Registry<T>> getResourceKey() { return this.resourceKey; }
        /** <strong>INTERNAL</strong>: Returns the {@link Codec} used for (de)serializing registry entries. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public Codec<T> getCodec() { return this.codec; }
        /** <strong>INTERNAL</strong>: Returns an {@link Optional} containing the network {@link Codec} if one was configured. */
        @ApiStatus.Internal
        @Deprecated(forRemoval = false)
        public Optional<Codec<T>> getNetCodec() { return Optional.ofNullable(this.netCodec); }
    }
}