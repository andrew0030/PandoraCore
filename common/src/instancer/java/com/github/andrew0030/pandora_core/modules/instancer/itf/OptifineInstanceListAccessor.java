package com.github.andrew0030.pandora_core.modules.instancer.itf;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;

public interface OptifineInstanceListAccessor {
	ObjectArrayList<LevelRenderer.RenderChunkInfo> getRenderInfosInstancer();
	
	ClientLevel getLevel();
}
