package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.attach;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.ItemAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemAttachments {
    @Unique
    InstancedItemRenderer pandoraCore$renderer;

    @Override
    public void pandoraCore$setInstancedRenderer(InstancedItemRenderer renderer) {
        this.pandoraCore$renderer = renderer;
    }

    @Override
    public InstancedItemRenderer pandoraCore$getInstancedRenderer() {
        return pandoraCore$renderer;
    }
}
