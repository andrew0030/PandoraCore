package com.github.andrew0030.pandora_core.modules.instancer.registry;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.ItemAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class InstancedRendererRegistry {
    private static final Map<BlockEntityType<?>, PaCoRegistryObject<? extends InstancedBlockEntityRenderer<?>>> BLOCK_ENTITY_RENDERERS = new HashMap<>();
    private static final Map<EntityType<?>, PaCoRegistryObject<? extends InstancedEntityRenderer<?>>> ENTITY_RENDERERS = new HashMap<>();
    private static final Map<Item, PaCoRegistryObject<InstancedItemRenderer>> ITEM_RENDERERS = new HashMap<>();

    // #################################################################################################################
    // #                                                Block Entities                                                 #
    // #################################################################################################################
    /**
     * Registers an {@link InstancedBlockEntityRenderer} for the specified {@link BlockEntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type    The {@link BlockEntityType} to associate with the instanced renderer.
     * @param factory The {@link Supplier} used to initialize the instanced renderer.
     */
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, Supplier<InstancedBlockEntityRenderer<T>> factory) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(factory, "factory is null");
        if (BLOCK_ENTITY_RENDERERS.putIfAbsent(type, new PaCoRegistryObject<>(factory)) != null)
            throw new IllegalArgumentException("Block Entity " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type) + " already has an instanced renderer attached!");
    }

    /**
     * Registers an {@link InstancedBlockEntityRenderer} for the specified {@link BlockEntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type     The {@link BlockEntityType} to associate with the instanced renderer.
     * @param renderer An {@link InstancedBlockEntityRenderer} instance.
     */
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, InstancedBlockEntityRenderer<T> renderer) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(renderer, "renderer is null");
        InstancedRendererRegistry.register(type, () -> renderer);
    }

    /**
     * Used to retrieve the {@link PaCoRegistryObject} containing the {@link InstancedBlockEntityRenderer}
     * instance, that corresponds to the given {@link BlockEntityType}.
     * <p>
     * <strong>NOTE:</strong> Avoid calling this method when possible, instead call {@link #getRenderer(BlockEntityType)}</p>
     */
    @Deprecated(forRemoval = false)
    public static <T extends BlockEntity> PaCoRegistryObject<? extends InstancedBlockEntityRenderer<T>> getRendererRegistry(BlockEntityType<T> type) {
        Objects.requireNonNull(type, "type is null");
        //noinspection unchecked
        return (PaCoRegistryObject<? extends InstancedBlockEntityRenderer<T>>) BLOCK_ENTITY_RENDERERS.get(type);
    }

    /** @return The {@link InstancedBlockEntityRenderer} attached to the given {@link BlockEntityType}, or {@code null} if none exists. */
    public static <T extends BlockEntity> InstancedBlockEntityRenderer<T> getRenderer(BlockEntityType<T> type) {
        Objects.requireNonNull(type, "type is null");
        //noinspection unchecked
        return (InstancedBlockEntityRenderer<T>) ((BlockEntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }

    // #################################################################################################################
    // #                                                   Entities                                                    #
    // #################################################################################################################
    /**
     * Registers an {@link InstancedEntityRenderer} for the specified {@link EntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type    The {@link EntityType} to associate with the instanced renderer.
     * @param factory The {@link Supplier} used to initialize the instanced renderer.
     */
    public static <T extends Entity> void register(EntityType<T> type, Supplier<InstancedEntityRenderer<?>> factory) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(factory, "factory is null");
        if (ENTITY_RENDERERS.putIfAbsent(type, new PaCoRegistryObject<>(factory)) != null)
            throw new IllegalArgumentException("Entity " + BuiltInRegistries.ENTITY_TYPE.getKey(type) + " already has an instanced renderer attached!");
    }

    /**
     * Registers an {@link InstancedEntityRenderer} for the specified {@link EntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type     The {@link EntityType} to associate with the instanced renderer.
     * @param renderer An {@link InstancedEntityRenderer} instance.
     */
    public static <T extends Entity> void register(EntityType<T> type, InstancedEntityRenderer<T> renderer) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(renderer, "renderer is null");
        InstancedRendererRegistry.register(type, () -> renderer);
    }

    /**
     * Used to retrieve the {@link PaCoRegistryObject} containing the {@link InstancedEntityRenderer}
     * instance, that corresponds to the given {@link EntityType}.
     * <p>
     * <strong>NOTE:</strong> Avoid calling this method when possible, instead call {@link #getRenderer(EntityType)}</p>
     */
    @Deprecated(forRemoval = false)
    public static <T extends Entity> PaCoRegistryObject<? extends InstancedEntityRenderer<T>> getRendererRegistry(EntityType<T> type) {
        Objects.requireNonNull(type, "type is null");
        //noinspection unchecked
        return (PaCoRegistryObject<? extends InstancedEntityRenderer<T>>) ENTITY_RENDERERS.get(type);
    }

    /** @return The {@link InstancedEntityRenderer} attached to the given {@link EntityType}, or {@code null} if none exists. */
    public static <T extends Entity> InstancedEntityRenderer<T> getRenderer(EntityType<T> type) {
        Objects.requireNonNull(type, "type is null");
        //noinspection unchecked
        return (InstancedEntityRenderer<T>) ((EntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }

    // #################################################################################################################
    // #                                                     Items                                                     #
    // #################################################################################################################
    /**
     * Registers an {@link InstancedItemRenderer} for the specified {@link ItemLike}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type    The {@link ItemLike} to associate with the instanced renderer.
     * @param factory The {@link Supplier} used to initialize the instanced renderer.
     */
    public static void register(ItemLike type, Supplier<InstancedItemRenderer> factory) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(factory, "factory is null");
        if (ITEM_RENDERERS.putIfAbsent(type.asItem(), new PaCoRegistryObject<>(factory)) != null)
            throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(type.asItem()) + " already has an instanced renderer attached!");
    }

    /**
     * Registers an {@link InstancedItemRenderer} for the specified {@link ItemLike}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type     The {@link ItemLike} to associate with the instanced renderer.
     * @param renderer An {@link InstancedItemRenderer} instance.
     */
    public static void register(ItemLike type, InstancedItemRenderer renderer) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(renderer, "renderer is null");
        InstancedRendererRegistry.register(type, () -> renderer);
    }

    /**
     * Used to retrieve the {@link PaCoRegistryObject} containing the {@link InstancedItemRenderer}
     * instance, that corresponds to the given {@link ItemLike}.
     * <p>
     * <strong>NOTE:</strong> Avoid calling this method when possible, instead call {@link #getRenderer(ItemLike)}</p>
     */
    @Deprecated(forRemoval = false)
    public static PaCoRegistryObject<InstancedItemRenderer> getRendererRegistry(ItemLike type) {
        Objects.requireNonNull(type, "type is null");
        return ITEM_RENDERERS.get(type.asItem());
    }

    /** @return The {@link InstancedItemRenderer} attached to the given {@link ItemLike}, or {@code null} if none exists. */
    public static InstancedItemRenderer getRenderer(ItemLike type) {
        Objects.requireNonNull(type, "type is null");
        return ((ItemAttachments) type.asItem()).pandoraCore$getInstancedRenderer();
    }

    // #################################################################################################################
    // #                                                Internal Stuff                                                 #
    // #################################################################################################################
    /**
     * <strong>DO NOT USE</strong>
     * <p>Use the other methods in this class!</p>
     */
    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    public static void registerInternal() {
        BLOCK_ENTITY_RENDERERS.forEach((type, factory) -> ((BlockEntityTypeAttachments) type).pandoraCore$setInstancedRenderer(factory.get()));
        ENTITY_RENDERERS.forEach((type, factory) -> ((EntityTypeAttachments) type).pandoraCore$setInstancedRenderer(factory.get()));
        ITEM_RENDERERS.forEach((type, factory) -> ((ItemAttachments) type).pandoraCore$setInstancedRenderer(factory.get()));
    }

    // TODO: Remove this method in the final release once TTC uses the ItemLike version!
    /**
     * <strong>NOTE:</strong> use {@link #register(ItemLike, InstancedItemRenderer)} instead.
     */
    @Deprecated(forRemoval = true)
    public static void register(Item type, InstancedItemRenderer renderer) {
        InstancedRendererRegistry.register(type, () -> renderer);
    }
}