package com.github.andrew0030.pandora_core.mixin.render.dynamic;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.item.IPaCoDynamicRendererTracker;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IPaCoDynamicRendererTracker {
    @Unique private boolean pandoraCore$isDynamicRendering;

    @Override
    public boolean pandoraCore$isDynamicRendering() {
        return pandoraCore$isDynamicRendering;
    }

    @Override
    public void pandoraCore$setDynamicRendering(boolean value) {
        pandoraCore$isDynamicRendering = value;
    }
}