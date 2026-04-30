package com.github.andrew0030.pandora_core.mixin.compat.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCheckTitleScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractElement.class)
public class FancyMenuAbstractElementMixin {

    // TODO: Add a config option to adjust which elements get hidden.

    @Inject(method = "_shouldRender()Z", at = @At("RETURN"), cancellable = true, remap = false)
    private void pandoraCore$checkTitleScreenHide(CallbackInfoReturnable<Boolean> cir) {
        Screen screen = AbstractElement.getScreen();
        if (screen != null && ((IPaCoCheckTitleScreen) screen).pandoraCore$isTitleScreen())
            if (((IPaCoModifyTitleScreen) screen).pandoraCore$areElementsHidden())
                cir.setReturnValue(false);
    }
}