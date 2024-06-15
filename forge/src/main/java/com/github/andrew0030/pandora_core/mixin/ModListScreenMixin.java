package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.gui.ModListScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ModListScreen.class, remap = false)
public class ModListScreenMixin implements IPaCoParentScreenGetter {
    @Shadow @Final private Screen parentScreen;

    @Override
    public Screen pandoraCore$getParentScreen() {
        return this.parentScreen;
    }
}