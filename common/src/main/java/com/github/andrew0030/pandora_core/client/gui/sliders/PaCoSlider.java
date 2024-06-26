package com.github.andrew0030.pandora_core.client.gui.sliders;

import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

// TODO: write javadoc for this class and make more slider classes (vertical slider and 2D slider).
public class PaCoSlider extends AbstractSliderButton {
    public static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
    // Slider Stuff
    protected boolean isTextHidden;
    protected double minValue;
    protected double maxValue;
    protected double stepSize;
    protected Component prefix;
    protected Component suffix;
    protected boolean isSilent;
    protected Integer sliderColor;
    protected Integer sliderRimColor;
    // Slider Indicator Stuff
    protected int indicatorWidth;
    protected int indicatorHeight;
    protected Integer indicatorColor;
    protected Integer indicatorRimColor;
    protected Integer indicatorHighlightedRimColor;
    // Slider Text Format
    private final DecimalFormat format;
    private Integer textColor;
    private Integer textColorInactive;

    public PaCoSlider(int x, int y, int width, int height, double minValue, double maxValue, double value, double stepSize) {
        super(x, y, Math.max(2, width), Math.max(height, 2), Component.empty(), value);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.indicatorWidth = 8;
        this.indicatorHeight = height;

        String pattern = !Mth.equal(this.stepSize, Mth.floor(this.stepSize)) ? Double.toString(this.stepSize).replaceAll("\\d", "0") : "0";
        this.format = new DecimalFormat(pattern);
        this.updateMessage();
    }

    public PaCoSlider setTextHidden(boolean isTextHidden) {
        this.isTextHidden = isTextHidden;
        this.updateMessage();
        return this;
    }

    public PaCoSlider setPrefix(Component prefix) {
        this.prefix = prefix;
        this.updateMessage();
        return this;
    }

    public PaCoSlider setSuffix(Component suffix) {
        this.suffix = suffix;
        this.updateMessage();
        return this;
    }

    public PaCoSlider setSilent() {
        this.isSilent = true;
        return this;
    }

    public PaCoSlider setIndicatorWidth(int width) {
        width = Math.max(2, width);
        this.indicatorWidth = width;
        return this;
    }

    public PaCoSlider setIndicatorHeight(int height) {
        height = Math.max(2, height);
        this.indicatorHeight = height;
        return this;
    }

    public PaCoSlider setSliderColor(int sliderColor, int sliderRimColor) {
        this.sliderColor = sliderColor;
        this.sliderRimColor = sliderRimColor;
        return this;
    }

    public PaCoSlider setIndicatorColor(int indicatorColor, int indicatorRimColor, int indicatorHighlightedRimColor) {
        this.indicatorColor = indicatorColor;
        this.indicatorRimColor = indicatorRimColor;
        this.indicatorHighlightedRimColor = indicatorHighlightedRimColor;
        return this;
    }

    /**
     * Used to set the text color, this slider should have.<br/>
     * This only sets the "{@link PaCoSlider#active}" color, if the slider can be deactivated use: {@link PaCoSlider#setTextColor(int, int)} <br/>
     * This ignores the alpha value of the given color, as the text is only affected by the {@link PaCoSlider#alpha} value of the slider.
     * @param textColor The color the text will have, {@link PaCoColor} can be used.
     */
    public PaCoSlider setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    /**
     * Used to set the text color, this slider should have.<br/>
     * If the slider is not "{@link PaCoSlider#active}" the second given color will be used.<br/>
     * This ignores the alpha value of the given color, as the text is only affected by the {@link PaCoSlider#alpha} value of the slider.
     * @param textColor The color the text will have, {@link PaCoColor} can be used.
     * @param textColorInactive The color the text will have if the slider is not "{@link PaCoSlider#active}", {@link PaCoColor} can be used.
     */
    public PaCoSlider setTextColor(int textColor, int textColorInactive) {
        this.textColor = textColor;
        this.textColorInactive = textColorInactive;
        return this;
    }

    public double getValue() {
        return this.value * (this.maxValue - this.minValue) + this.minValue;
    }

    public void setValue(double value) {
        this.value = this.snapToNearest((value - this.minValue) / (this.maxValue - this.minValue));
        this.updateMessage();
    }

    public String getAsString() {
        return this.format.format(this.getValue());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseX);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (!this.isSilent) // Prevent click sound if needed
            super.onRelease(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == GLFW.GLFW_KEY_LEFT;
        if (flag || keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (this.minValue > this.maxValue)
                flag = !flag;
            this.setValue(this.getValue() + (this.stepSize * (flag ? -1F : 1F)));
            return true;
        }
        return false;
    }

    protected void setValueFromMouse(double mouseX) {
        this.setSliderValue((mouseX - (double)(this.getX() + (this.indicatorWidth / 2F))) / (double)(this.width - this.indicatorWidth));
    }

    protected void setSliderValue(double value) {
        double oldValue = this.value;
        this.value = this.snapToNearest(value);
        if (!Mth.equal(oldValue, this.value))
            this.applyValue();
        this.updateMessage();
    }

    protected double snapToNearest(double value) {
        if (this.stepSize <= 0.0) return Mth.clamp(value, 0.0, 1.0);

        value = Mth.lerp(Mth.clamp(value, 0.0, 1.0), this.minValue, this.maxValue);
        value = this.stepSize * Math.round(value / this.stepSize);

        double minClamp = Math.min(this.minValue, this.maxValue);
        double maxClamp = Math.max(this.minValue, this.maxValue);
        value = Mth.clamp(value, minClamp, maxClamp);

        return Mth.map(value, this.minValue, this.maxValue, 0.0, 1.0);
    }

    @Override
    protected void updateMessage() {
        MutableComponent component = Component.literal("");
        if (!this.isTextHidden) {
            if (this.prefix != null) component.append(this.prefix);
            component.append(this.getAsString());
            if (this.suffix != null) component.append(this.suffix);
        }
        this.setMessage(component);
    }

    @Override
    protected void applyValue() {}

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        // Slider
        if (this.sliderColor != null || this.sliderRimColor != null) {
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.sliderColor, this.sliderRimColor, 1);
        } else {
            graphics.blitNineSliced(SLIDER_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, 0);
        }
        // Slider Indicator
        int posX = this.getX() + (int)(this.value * (double)(this.width - this.indicatorWidth));
        int posY = this.getY() + (this.height - this.indicatorHeight) / 2;
        if (this.indicatorColor != null || this.indicatorRimColor != null) {
            int rimColor = this.shouldHighlight() ? this.indicatorHighlightedRimColor : this.indicatorRimColor;
            PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, this.indicatorWidth, this.indicatorHeight, this.indicatorColor, rimColor, 1);
        } else {
            graphics.blitNineSliced(SLIDER_LOCATION, posX, posY, this.indicatorWidth, this.indicatorHeight, 20, 4, 200, 20, 0, this.getIndicatorTextureY());
        }

        int color = this.active ?
                (this.textColor != null ? this.textColor : 16777215) :
                (this.textColorInactive != null ? this.textColorInactive : 10526880);
        this.renderScrollingString(graphics, minecraft.font, 2, color | Mth.ceil(this.alpha * 255.0F) << 24);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int getIndicatorTextureY() {
        return this.shouldHighlight() ? 60 : 40;
    }

    private boolean shouldHighlight() {
        return this.isHovered;
    }
}