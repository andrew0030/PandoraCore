package com.github.andrew0030.pandora_core.client.gui.buttons;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ModsFilterButton extends AbstractButton {
    private static final FilterType[] FILTER_TYPES = FilterType.values();
    private FilterType filterType = FilterType.NONE;

    public ModsFilterButton(int x, int y) {
        super(x, y, 18, 18, Component.empty());
        this.setMessage(Component.translatable("gui.pandora_core.paco.filter.tooltip", this.filterType.getText()));
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int u = this.filterType.ordinal() * 18;
        int v = 0;
        if (this.isHoveredOrFocused()) {
            v += 18;
            Component tooltip = Component.translatable("gui.pandora_core.paco.filter.tooltip", this.filterType.getText());
            graphics.renderTooltip(Minecraft.getInstance().font, tooltip, this.getX() + 10, this.getY() + 17);
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        graphics.blit(PaCoScreen.TEXTURE, this.getX(), this.getY(), u, v, this.getWidth(), this.getHeight());
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onPress() {
        this.nextFilterType();
        this.setMessage(Component.translatable("gui.pandora_core.paco.filter.tooltip", this.filterType.getText()));
    }

    /** Cycles the {@link ModsFilterButton#filterType} */
    private void nextFilterType() {
        int currentIdx = this.filterType.ordinal();
        int nextIdx = (currentIdx + 1) % FILTER_TYPES.length;
        this.filterType = FILTER_TYPES[nextIdx];
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public enum FilterType {
        NONE(Component.translatable("gui.pandora_core.paco.filter.tooltip.none")),
        WARNINGS(Component.translatable("gui.pandora_core.paco.filter.tooltip.warnings")),
        UPDATES(Component.translatable("gui.pandora_core.paco.filter.tooltip.updates"));

        private final Component text;

        FilterType(Component text) {
            this.text = text;
        }

        public Component getText() {
            return this.text;
        }
    }
}