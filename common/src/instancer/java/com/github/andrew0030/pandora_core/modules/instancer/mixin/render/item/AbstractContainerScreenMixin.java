package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.item;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin.ItemInstancingEnv;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstancingEnvironment;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
	@Unique InstanceManager pandoraCore$manager;
	@Unique InstancingEnvironment pandoraCore$env;
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hoveredSlot:Lnet/minecraft/world/inventory/Slot;", shift = At.Shift.AFTER, ordinal = 0), method = "render")
	public void postHoveredSlot(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		pandoraCore$manager = new InstanceManager();
		pandoraCore$env = new ItemInstancingEnv(pandoraCore$manager);
		PaCoRenderState.ACTIVE_ENVIRONMENT = pandoraCore$env;
		pandoraCore$manager.markFrame();
	}
	
	@Inject(at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V",
			shift = At.Shift.BEFORE
	), method = "render", require = 0)
	public void preRenderHighlight(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		pandoraCore$manager.drawFrame(pandoraCore$env);
		pandoraCore$manager.markFrame();
	}
	
	@Inject(at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V",
			shift = At.Shift.BEFORE
	), method = "render", require = 0)
	public void preRenderHighlightForge(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		pandoraCore$manager.drawFrame(pandoraCore$env);
		pandoraCore$manager.markFrame();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", shift = At.Shift.BEFORE), method = "render")
	public void preRenderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		pandoraCore$manager.drawFrame(pandoraCore$env);
		pandoraCore$manager.close();
		pandoraCore$manager = null;
		pandoraCore$env = null;
		PaCoRenderState.ACTIVE_ENVIRONMENT = null;
	}
}