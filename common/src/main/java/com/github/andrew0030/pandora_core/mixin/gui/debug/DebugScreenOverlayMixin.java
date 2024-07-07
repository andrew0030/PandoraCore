package com.github.andrew0030.pandora_core.mixin.gui.debug;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(at = @At("TAIL"), method = "getSystemInformation")
    public void postSys(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().add("");
        cir.getReturnValue().add(ChatFormatting.GOLD + "[Pandora Core]");
        TemplateManager.writeF3(cir.getReturnValue());
    }
}