package com.github.andrew0030.pandora_core.mixin.gui;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoUpdateTooltip;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements IPaCoUpdateTooltip {
    @Shadow protected abstract void updateTooltip();

    @Override
    public void pandoraCore$updateTooltip() {
        this.updateTooltip();
    }
}