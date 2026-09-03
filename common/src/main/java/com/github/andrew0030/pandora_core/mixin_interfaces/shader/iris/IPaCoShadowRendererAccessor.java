package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;

public interface IPaCoShadowRendererAccessor {
    ClientLevel getActiveLevel();
    LevelRendererAccessor getRenderer();
	Frustum getEntityFrustum();
}
