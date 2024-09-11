package com.github.andrew0030.pandora_core.client.gui.sliders;

import net.minecraft.client.gui.navigation.CommonInputs;
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
        int handleY = this.getHandleY();
        boolean mouseOverHandle = mouseY >= handleY && mouseY < handleY + this.handleHeight;
        this.clickOffset = 0;
        if(mouseOverHandle && !this.centerHandle)
            this.clickOffset = (mouseY - handleY) - this.handleHeight / 2D;
        this.setValueFromMouse(mouseY - this.clickOffset);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseY - this.clickOffset);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Checks if the pressed key was Enter/Space
        if (CommonInputs.selected(keyCode)) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        }
        // If the slider was "selected" we check the input and modify the value accordingly
        if (this.canChangeValue) {
            boolean isUpKey = keyCode == GLFW.GLFW_KEY_UP;
            boolean isDownKey = keyCode == GLFW.GLFW_KEY_DOWN;
            if (isUpKey || isDownKey) {
                int direction = (isUpKey ? -1 : 1) * (this.minValue > this.maxValue ? -1 : 1);
                this.setValue(this.getValue() + (this.stepSize * direction));
                return true;
            }
        }
        // If the slider wasn't "selected" we just return as we don't want to modify its value.
        return false;
    }

    @Override
    protected void setValueFromMouse(double mouseY) {
        this.setSliderValue((mouseY - (double)(this.getY() + (this.handleHeight / 2F))) / (double)(this.height - this.handleHeight));
    }

    @Override
    public int getHandleX() {
        return this.getX() + (this.width - this.handleWidth) / 2;
    }

    @Override
    public int getHandleY() {
        return this.getY() + (int)(this.value * (double)(this.height - this.handleHeight));
    }
}