package com.github.andrew0030.pandora_core.mixin.render.dynamic;

import com.github.andrew0030.pandora_core.client.render.dynamic.PaCoDynamicItemRenderer;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.item.IPaCoDynamicItemRenderer;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements IPaCoDynamicItemRenderer {
    @Unique private PaCoDynamicItemRenderer pandoraCore$dynamicItemRenderer;

    public void pandoraCore$setDynamicItemRenderer(PaCoDynamicItemRenderer renderer) {
        this.pandoraCore$dynamicItemRenderer = renderer;
    }

    @Override
    public PaCoDynamicItemRenderer pandoraCore$getDynamicItemRenderer() {
        return this.pandoraCore$dynamicItemRenderer;
    }
}