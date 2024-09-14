package com.github.andrew0030.pandora_core.mixin.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public class EditBoxMixin implements IPaCoEditBox {

    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0), index = 4)
    public int modifyEditBoxRimColor(int color) {
        return this.pandoraCore$hideRim() ? PaCoColor.NO_ALPHA : color;
    }

    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 1), index = 4)
    public int modifyEditBoxColor(int color) {
        return this.pandoraCore$hideBackground() ? PaCoColor.NO_ALPHA : color;
    }

    @Inject(method = "onValueChange", at = @At("HEAD"))
    public void injectOnValueChange(String newText, CallbackInfo ci) {
        this.pandoraCore$onValueChange(newText);
    }

    @Override
    public boolean pandoraCore$hideBackground() {
        return false;
    }

    @Override
    public boolean pandoraCore$hideRim() {
        return false;
    }

    @Override
    public void pandoraCore$onValueChange(String newText) {}
}