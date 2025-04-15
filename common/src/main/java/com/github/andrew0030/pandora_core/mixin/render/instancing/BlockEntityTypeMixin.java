package com.github.andrew0030.pandora_core.mixin.render.instancing;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin implements BlockEntityTypeAttachments {
    @Unique
    InstancedBlockEntityRenderer<?> pandoraCore$renderer;

    @Override
    public void pandoraCore$setInstancedRenderer(InstancedBlockEntityRenderer<?> renderer) {
        this.pandoraCore$renderer = renderer;
    }

    @Override
    public InstancedBlockEntityRenderer<?> pandoraCore$getInstancedRenderer() {
        return pandoraCore$renderer;
    }
}
