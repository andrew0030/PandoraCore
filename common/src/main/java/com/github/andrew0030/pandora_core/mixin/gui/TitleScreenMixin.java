package com.github.andrew0030.pandora_core.mixin.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCheckTitleScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin implements IPaCoCheckTitleScreen, IPaCoModifyTitleScreen {
    @Unique private boolean pandoraCore$hideElements;

    @Inject(method = "init", at = @At("HEAD"))
    public void injectSafetyVisibilitySetter(CallbackInfo ci) {
        this.pandoraCore$hideElements = false;
    }

    @Override
    public boolean pandoraCore$isTitleScreen() {
        return true;
    }

    @Override
    public void pandoraCore$hideElements(boolean value) {
        this.pandoraCore$hideElements = value;
    }

    @Override
    public boolean pandoraCore$areElementsHidden() {
        return this.pandoraCore$hideElements;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/LogoRenderer;renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IF)V", ordinal = 0), cancellable = true)
    public void injectAboveLogoRenderer(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.pandoraCore$hideElements)
            ci.cancel();
    }
}