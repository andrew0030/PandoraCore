package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCheckTitleScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public class TitleScreenMixin implements IPaCoCheckTitleScreen {

    @Override
    public boolean isPaCoTitleScreen() {
        return true;
    }
}