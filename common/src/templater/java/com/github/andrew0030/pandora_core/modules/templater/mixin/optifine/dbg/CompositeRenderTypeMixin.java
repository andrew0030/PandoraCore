package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.dbg;

import com.github.andrew0030.pandora_core.test.InstancingTestBlockEntityRenderer;
import com.github.andrew0030.pandora_core.utils.debug.RenderDebugger;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public class CompositeRenderTypeMixin {
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState state, CallbackInfo ci) {
		((RenderTypeAccessor) this).setSetupState(() -> {
			((CompositeStateAccessor) (Object) state).getStates().forEach((s) -> {
				s.setupRenderState();
				RenderDebugger.checkError("Setup" + s.toString());
			});
		});
	}
}
