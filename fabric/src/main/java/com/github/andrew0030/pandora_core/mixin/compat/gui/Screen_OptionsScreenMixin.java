package com.github.andrew0030.pandora_core.mixin.compat.gui;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin.accessor.OptionsScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public class Screen_OptionsScreenMixin {
    @Shadow @Nullable protected Minecraft minecraft;

    /**
     * On <b>Forge</b> there is a patch that checks if the {@code lastScreen} was {@code PauseScreen},
     * if it wasn't instead of setting the screen to {@code null}, this returns to {@link PaCoScreen}.
     */
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    public void returnToPaCoScreen(CallbackInfo ci) {
        if (((Screen)(Object)this) instanceof OptionsScreen optionsScreen) {
            Screen lastScreen = ((OptionsScreenAccessor) optionsScreen).getLastScreen();
            if (lastScreen instanceof PaCoScreen) {
                this.minecraft.setScreen(lastScreen);
                ci.cancel();
            }
        }
    }
}