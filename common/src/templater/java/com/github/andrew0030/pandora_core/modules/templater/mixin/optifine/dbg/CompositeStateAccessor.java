package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.dbg;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeState.class)
public interface CompositeStateAccessor {
	@Accessor("states")
	ImmutableList<RenderStateShard> getStates();
}
