package com.github.andrew0030.pandora_core.modules.instancer.registry;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.ItemAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InstancedRendererRegistry {
    private static final List<Pair<BlockEntityType<?>, Supplier< ? extends InstancedBlockEntityRenderer<?>>>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
    private static final List<Pair<EntityType<?>, Supplier<? extends InstancedEntityRenderer<?>>>> ENTITY_RENDERERS = new ArrayList<>();
    private static final List<Pair<Item, Supplier<InstancedItemRenderer>>> ITEM_RENDERERS = new ArrayList<>();

    /**
     * Registers an {@link InstancedBlockEntityRenderer} for the specified {@link BlockEntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type    The {@link BlockEntityType} to associate with the custom renderer.
     * @param factory The {@link Supplier} used to initialize the renderer.
     */
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, Supplier<InstancedBlockEntityRenderer<T>> factory) {
        BLOCK_ENTITY_RENDERERS.add(new Pair<>(type, factory));
    }

    /**
     * Registers an {@link InstancedBlockEntityRenderer} for the specified {@link BlockEntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type     The {@link BlockEntityType} to associate with the custom renderer.
     * @param renderer An {@link InstancedBlockEntityRenderer} instance.
     */
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, InstancedBlockEntityRenderer<T> renderer) {
        InstancedRendererRegistry.register(type, () -> renderer);
    }

    /** @return The {@link InstancedBlockEntityRenderer} attached to the given {@link BlockEntityType}, or {@code null} if none exists. */
    public static <T extends BlockEntity> InstancedBlockEntityRenderer<T> getRenderer(BlockEntityType<T> type) {
        //noinspection unchecked
        return (InstancedBlockEntityRenderer<T>) ((BlockEntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }

    /**
     * Registers an {@link InstancedEntityRenderer} for the specified {@link EntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type    The {@link EntityType} to associate with the custom renderer.
     * @param factory The {@link Supplier} used to initialize the renderer.
     */
    public static <T extends Entity> void register(EntityType<T> type, Supplier<InstancedEntityRenderer<?>> factory) {
        ENTITY_RENDERERS.add(new Pair<>(type, factory));
    }

    /**
     * Registers an {@link InstancedEntityRenderer} for the specified {@link EntityType}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type     The {@link EntityType} to associate with the custom renderer.
     * @param renderer An {@link InstancedEntityRenderer} instance.
     */
    public static <T extends Entity> void register(EntityType<T> type, InstancedEntityRenderer<T> renderer) {
        InstancedRendererRegistry.register(type, () -> renderer);
    }

    /** @return The {@link InstancedEntityRenderer} attached to the given {@link EntityType}, or {@code null} if none exists. */
    public static <T extends Entity> InstancedEntityRenderer<T> getRenderer(EntityType<T> type) {
        //noinspection unchecked
        return (InstancedEntityRenderer<T>) ((EntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }

    /**
     * Registers an {@link InstancedItemRenderer} for the specified {@link Item}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type    The {@link Item} to associate with the custom renderer.
     * @param factory The {@link Supplier} used to initialize the renderer.
     */
    public static void register(Item type, Supplier<InstancedItemRenderer> factory) {
        ITEM_RENDERERS.add(new Pair<>(type, factory));
    }

    /**
     * Registers an {@link InstancedItemRenderer} for the specified {@link Item}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param type     The {@link Item} to associate with the custom renderer.
     * @param renderer An {@link InstancedItemRenderer} instance.
     */
    public static void register(Item type, InstancedItemRenderer renderer) {
        InstancedRendererRegistry.register(type, () -> renderer);
    }

    /** @return The {@link InstancedItemRenderer} attached to the given {@link Item}, or {@code null} if none exists. */
    public static InstancedItemRenderer getRenderer(Item type) {
        //noinspection unchecked
        return ((ItemAttachments) type).pandoraCore$getInstancedRenderer();
    }

    /**
     * <strong>DO NOT USE</strong>
     * <p>Use the other methods in this class!</p>
     */
    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    public static void registerInternal() {
        InstancedRendererRegistry.BLOCK_ENTITY_RENDERERS.forEach(pair -> ((BlockEntityTypeAttachments) pair.getFirst()).pandoraCore$setInstancedRenderer(pair.getSecond().get()));
        InstancedRendererRegistry.ENTITY_RENDERERS.forEach(pair -> ((EntityTypeAttachments) pair.getFirst()).pandoraCore$setInstancedRenderer(pair.getSecond().get()));
        InstancedRendererRegistry.ITEM_RENDERERS.forEach(pair -> ((ItemAttachments) pair.getFirst()).pandoraCore$setInstancedRenderer(pair.getSecond().get()));
    }
}