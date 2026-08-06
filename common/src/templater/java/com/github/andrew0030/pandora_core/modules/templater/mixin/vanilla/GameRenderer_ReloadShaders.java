package com.github.andrew0030.pandora_core.modules.templater.mixin.vanilla;

import com.github.andrew0030.pandora_core.modules.templater.TemplateManager;
import com.github.andrew0030.pandora_core.modules.templater.loader.impl.VanillaTemplateLoader;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderer_ReloadShaders {
	@Inject(at = @At("RETURN"), method = "reloadShaders")
	public void postReload(ResourceProvider resourceProvider, CallbackInfo ci) {
		VanillaTemplateLoader.getInstance().dumpShaders();
		TemplateManager.reloadLoader(VanillaTemplateLoader.getInstance());
	}
}
