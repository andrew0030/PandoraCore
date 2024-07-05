package com.github.andrew0030.pandora_core.mixin.compat.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Catalogue Mods Screen Mixin */
@Mixin(value = CatalogueModListScreen.class, remap = false)
public class CatalogueModListScreenMixin implements IPaCoParentScreenGetter {
    @Shadow @Final private Screen parentScreen;

    @Override
    public Screen pandoraCore$getParentScreen() {
        return this.parentScreen;
    }
}