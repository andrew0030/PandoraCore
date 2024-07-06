package com.github.andrew0030.pandora_core.mixin.render;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleUsage;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexBuffer.Usage.class)
public class UsageAccessorMixin implements IPaCoAccessibleUsage {
    @Shadow @Final private int id;

    @Override
    public int pandoraCore$id() {
        return id;
    }
}
