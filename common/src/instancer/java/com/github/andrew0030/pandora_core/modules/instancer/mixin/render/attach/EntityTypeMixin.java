package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.attach;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class EntityTypeMixin implements EntityTypeAttachments {
    @Unique
    InstancedEntityRenderer<?> pandoraCore$renderer;

    @Override
    public void pandoraCore$setInstancedRenderer(InstancedEntityRenderer<?> renderer) {
        this.pandoraCore$renderer = renderer;
    }

    @Override
    public InstancedEntityRenderer<?> pandoraCore$getInstancedRenderer() {
        return pandoraCore$renderer;
    }
}
