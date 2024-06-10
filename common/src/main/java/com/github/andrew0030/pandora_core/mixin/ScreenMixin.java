package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.client.key.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCheckTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin implements IPaCoCheckTitleScreen {

    @Override
    public boolean isPaCoTitleScreen() {
        return false;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void titleScreenPaCoKeyBind(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (((IPaCoCheckTitleScreen) (Object) this).isPaCoTitleScreen()) {
            if (PaCoKeyMappings.KEY_PACO.matches(keyCode, scanCode))
                Minecraft.getInstance().setScreen(new PaCoScreen((TitleScreen) (Object) this));
        }
    }
}