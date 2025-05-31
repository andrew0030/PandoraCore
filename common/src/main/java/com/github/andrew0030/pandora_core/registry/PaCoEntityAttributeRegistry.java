package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple, cross-platform registry helper used for specifying entity attributes
 * for {@link EntityType} instances, in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoEntityAttributeRegistry ENTITY_ATTRIBUTES = new PaCoEntityAttributeRegistry();
 * static {
 *     ENTITY_ATTRIBUTES.add(ExampleModEntities.EXAMPLE_ENTITY, () -> ExampleEntity.createAttributes().build());
 * }
 * }</pre>
 *
 * <p>And then during mod construction:</p>
 * <pre>{@code
 * ExampleModEntityAttributes.ENTITY_ATTRIBUTES.register();
 * }</pre>
 */
public class PaCoEntityAttributeRegistry {
    private final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> entityAttributes = new HashMap<>();

    /**
     * Registers attribute definitions for a specific {@link EntityType} that extends {@link LivingEntity}.
     *
     * @param entity     a {@link Supplier} of the {@link EntityType} to register attributes for.
     * @param attributes a {@link Supplier} of the {@link AttributeSupplier} defining the attributes
     *                   associated with the given entity type.
     */
    public <T extends LivingEntity> void add(Supplier<EntityType<T>> entity, Supplier<AttributeSupplier> attributes) {
        this.entityAttributes.put(entity, attributes);
    }

    /**
     * This needs to be called, so event listeners are created by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ModInitializer#onInitialize}.<br/>
     */
    public void register() {
        Services.REGISTRY.registerEntityAttributes(this.entityAttributes);
    }
}