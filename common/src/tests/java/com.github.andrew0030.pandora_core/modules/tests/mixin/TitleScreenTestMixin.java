package com.github.andrew0030.pandora_core.modules.tests.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO remove after testing
@Mixin(TitleScreen.class)
public class TitleScreenTestMixin {

    @Inject(method = "init", at = @At("TAIL"))
    public void injectTestMixin(CallbackInfo ci) {
        System.out.println("Title Screen Test Mixin Called!!!");
    }
}