package com.github.andrew0030.pandora_core.client.gui.sliders;

import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class PaCoVerticalSlider extends PaCoSlider {

    /**
     * @param x        The x position of upper left corner
     * @param y        The y position of upper left corner
     * @param width    The width of the slider
     * @param height   The height of the slider
     * @param minValue The minimum (top) value of the slider
     * @param maxValue The maximum (bottom) value of the slider
     * @param value    The value of the slider when its first displayed
     * @param stepSize Size of steps used
     */
    public PaCoVerticalSlider(int x, int y, int width, int height, double minValue, double maxValue, double value, double stepSize) {
        super(x, y, width, height, minValue, maxValue, value, stepSize);
        this.handleWidth = width;
        this.handleHeight = 8;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseY);
    }

    //TODO there is a bit of jank if more than 1 slider is in the UI, need to make sure only focused moves with key input
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == GLFW.GLFW_KEY_UP;
        if (flag || keyCode == GLFW.GLFW_KEY_DOWN) {
            if (this.minValue > this.maxValue)
                flag = !flag;
            this.setValue(this.getValue() + (this.stepSize * (flag ? -1F : 1F)));
            return true;
        }
        return false;
    }

    @Override
    protected void setValueFromMouse(double mouseY) {
        this.setSliderValue((mouseY - (double)(this.getY() + (this.handleHeight / 2F))) / (double)(this.height - this.handleHeight));
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        boolean mouseOverSlider = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        boolean mouseOverHandle = false;
        // Only check if the handle is bigger than the slider
        if (this.handleWidth > this.width || this.handleHeight > this.height) {
            int posX = this.getX() + (this.width - this.handleWidth) / 2;
            int posY = this.getY() + (int)(this.value * (double)(this.height - this.handleHeight));
            mouseOverHandle = mouseX >= posX && mouseY >= posY && mouseX < posX + this.handleWidth && mouseY < posY + this.handleHeight;
        }
        return this.active && this.visible && (mouseOverSlider || mouseOverHandle);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            if (this.handleWidth > this.width || this.handleHeight > this.height) {
                int posX = this.getX() + (this.width - this.handleWidth) / 2;
                int posY = this.getY() + (int)(this.value * (double)(this.height - this.handleHeight));
                this.isHovered = this.isHovered || mouseX >= posX && mouseY >= posY && mouseX < posX + this.handleWidth && mouseY < posY + this.handleHeight;
            }
            this.renderWidget(graphics, mouseX, mouseY, pPartialTick);
            this.updateTooltip();
        }
    }

    @Override
    protected void renderSliderhandle(GuiGraphics graphics) {
        int posX = this.getX() + (this.width - this.handleWidth) / 2;
        int posY = this.getY() + (int)(this.value * (double)(this.height - this.handleHeight));
        if (this.handleColor != null || this.handleRimColor != null) {
            int rimColor = this.shouldHighlight() ? this.handleHighlightedRimColor : this.handleRimColor;
            PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, this.handleWidth, this.handleHeight, this.handleColor, rimColor, 1);
        } else {
            graphics.blitNineSliced(SLIDER_LOCATION, posX, posY, this.handleWidth, this.handleHeight, 20, 4, 200, 20, 0, this.getSliderHandleTextureY());
        }
    }
}