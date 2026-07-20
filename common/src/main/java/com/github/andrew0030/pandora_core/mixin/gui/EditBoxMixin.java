package com.github.andrew0030.pandora_core.mixin.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EditBoxMixin implements IPaCoEditBox {
    @Shadow @Final private static int CURSOR_INSERT_WIDTH;
    @Shadow @Final private static int CURSOR_INSERT_COLOR;
    @Shadow private boolean bordered;
    @Shadow private int displayPos;
    @Shadow private int highlightPos;

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

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", ordinal = 1))
    private int injectRenderWidget(GuiGraphics instance, Font font, String text, int x, int y, int color, Operation<Integer> original) {
        // Checks if the EditBox should always render a line indicator
        if (this.pandoraCore$forceLineIndicator()) {
            instance.fill(
                    RenderType.guiOverlay(),
                    x, y - 1,
                    x + CURSOR_INSERT_WIDTH, y + 1 + 9,
                    CURSOR_INSERT_COLOR
            );
            return 0; // Dummy integer, the original code ignores the value so anything should be fine...
        }
        // If the line indicator isn't force rendered, we simply delegate to vanilla
        return original.call(instance, font, text, x, y, color);
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
    public boolean pandoraCore$forceLineIndicator() {
        return false;
    }

    @Override
    public void pandoraCore$onValueChange(String newText) {}

    @Override
    public boolean pandoraCore$isBordered() {
        return this.bordered;
    }

    @Override
    public int pandoraCore$getDisplayPos() {
        return this.displayPos;
    }

    @Override
    public void pandoraCore$setDisplayPos(int displayPos) {
        this.displayPos = displayPos;
    }

    @Override
    public int pandoraCore$getHighlightPos() {
        return this.highlightPos;
    }

    @Override
    public void pandoraCore$setHighlightPos(int highlightPos) {
        this.highlightPos = highlightPos;
    }
}