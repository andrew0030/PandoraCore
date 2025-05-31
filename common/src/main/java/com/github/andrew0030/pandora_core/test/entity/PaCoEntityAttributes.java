package com.github.andrew0030.pandora_core.test.entity;

import com.github.andrew0030.pandora_core.registry.PaCoEntityAttributeRegistry;

public class PaCoEntityAttributes {
    public static final PaCoEntityAttributeRegistry ENTITY_ATTRIBUTES = new PaCoEntityAttributeRegistry();

    static {
        ENTITY_ATTRIBUTES.add(PaCoEntities.TEST_ENTITY, () -> TestEntity.createAttributes().build());
    }
}