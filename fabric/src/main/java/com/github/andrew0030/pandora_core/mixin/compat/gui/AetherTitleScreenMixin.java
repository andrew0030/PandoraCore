package com.github.andrew0030.pandora_core.mixin.compat.gui;

import com.aetherteam.aether.client.gui.screen.menu.AetherTitleScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin targets Aether's {@code TitleScreen} replacement class,
 * in order to conditionally disable the element rendering if needed.<br/>
 * The reason we do this is, so we can have a clean background behind {@code PaCoScreen}.
 */
@Mixin(AetherTitleScreen.class)
public class AetherTitleScreenMixin {
    /*
     * NOTE: Since AetherTitleScreen extends TitleScreen and overrides Screen#render(),
     * we actually don't need to (remap = false) since the method is a vanilla method.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/aetherteam/aether/client/gui/screen/menu/AetherTitleScreen;setupLogo(Lnet/minecraft/client/gui/GuiGraphics;FF)V"), cancellable = true)
    public void injectAboveLogoRenderer(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (((IPaCoModifyTitleScreen) this).pandoraCore$areElementsHidden())
            ci.cancel();
    }
}