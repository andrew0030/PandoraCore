package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.dbg;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderTypeAccessor {
	@Accessor("setupState")
	void setSetupState(Runnable r);
}
