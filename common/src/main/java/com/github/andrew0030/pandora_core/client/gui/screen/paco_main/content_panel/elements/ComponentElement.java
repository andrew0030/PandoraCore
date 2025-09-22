package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;

//TODO: redo how click handling works because the text click event is affected by chat settings
// Note: setting the link source to trusted in the method that opens them should get rid of the "don't open links you don't trust" message
public class ComponentElement extends BaseContentElement {
    private final Component component;
    private final Component underlinedComponent;

    public ComponentElement(PaCoContentPanelManager manager, Component component) {
        this(manager, 0, 0, component);
    }

    public ComponentElement(PaCoContentPanelManager manager, int offsetX, int offsetY, Component component) {
        super(manager, offsetX, offsetY);
        this.component = component;
        this.underlinedComponent = ComponentUtils.mergeStyles(component.copy(), Style.EMPTY.withUnderlined(true));
        this.initializeHeight();
    }

    public Component getComponent() {
        return this.component;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Component
        int textWidth = this.font.width(component);
        int textHeight = this.font.lineHeight;
        boolean isHovered = mouseX >= this.getX() && mouseX < this.getX() + textWidth && mouseY >= this.getY() && mouseY < this.getY() + textHeight;
        Component component = isHovered ? this.underlinedComponent : this.component;
        graphics.drawString(this.font, component, this.getX(), this.getY(), PaCoColor.WHITE, true);

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE)
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 128, 0), 1);
    }

    @Override
    public int getHeight() {
        return 10;
    }
}