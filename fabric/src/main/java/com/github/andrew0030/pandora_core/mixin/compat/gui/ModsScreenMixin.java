package com.github.andrew0030.pandora_core.mixin.compat.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** ModMenu Mods Screen Mixin */
@Mixin(value = ModsScreen.class, remap = false)
public class ModsScreenMixin implements IPaCoParentScreenGetter {
    @Shadow @Final private Screen previousScreen;

    @Override
    public Screen pandoraCore$getParentScreen() {
        return this.previousScreen;
    }
}