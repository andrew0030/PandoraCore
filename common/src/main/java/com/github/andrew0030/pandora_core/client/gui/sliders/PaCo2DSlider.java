package com.github.andrew0030.pandora_core.client.gui.sliders;

import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoUpdateTooltip;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

public class PaCo2DSlider extends AbstractSliderButton
{
    public static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
    // Slider Stuff
    protected double minValueX;
    protected double maxValueX;
    protected double stepSizeX;
    protected double minValueY;
    protected double maxValueY;
    protected double valueY;
    protected double stepSizeY;
    protected boolean isSilent = false;
    protected int sliderTextureWidth;
    protected int sliderTextureHeight;
    protected int sliderSliceSize;
    protected ResourceLocation sliderTexture;
    protected int sliderU;
    protected int sliderV;
    protected ResourceLocation sliderHighlightedTexture;
    protected int sliderHighlightedU;
    protected int sliderHighlightedV;
    protected Integer sliderColor;
    protected Integer sliderRimColor;
    protected Integer sliderHighlightedRimColor;
    protected Component prefix;
    protected Component interfix;
    protected Component suffix;
    // Slider Handle Stuff
    protected int handleWidth;
    protected int handleHeight;
    protected int handleTextureWidth;
    protected int handleTextureHeight;
    protected int handleSliceSize;
    protected ResourceLocation handleTexture;
    protected int handleU;
    protected int handleV;
    protected ResourceLocation handleHighlightedTexture;
    protected int handleHighlightedU;
    protected int handleHighlightedV;
    protected Integer handleColor;
    protected Integer handleRimColor;
    protected Integer handleHighlightedRimColor;
    // Slider Text Stuff
    private final DecimalFormat formatX;
    private final DecimalFormat formatY;
    protected HorizontalTextSnap horizontalTextSnap = HorizontalTextSnap.CENTER;
    protected VerticalTextSnap verticalTextSnap = VerticalTextSnap.CENTER;
    protected int textOffsetX;
    protected int textOffsetY;
    protected Integer textColor;
    protected Integer textColorInactive;
    protected boolean isTextHidden = false;
    protected boolean dropShadow = true;
    protected boolean zeroPad = false;

    /**
     * @param x         The x position of upper left corner
     * @param y         The y position of upper left corner
     * @param width     The width of the slider
     * @param height    The height of the slider
     * @param minValueX The minimum x-axis value of the slider (left)
     * @param maxValueX The maximum x-axis value of the slider (right)
     * @param valueX    The x-axis value of the slider when its first displayed
     * @param stepSizeX Size of steps used on x-axis
     * @param minValueY The minimum x-axis value of the slider (left)
     * @param maxValueY The maximum x-axis value of the slider (right)
     * @param valueY    The x-axis value of the slider when its first displayed
     * @param stepSizeY Size of steps used on x-axis
     */
    public PaCo2DSlider(int x, int y, int width, int height, double minValueX, double maxValueX, double valueX, double stepSizeX, double minValueY, double maxValueY, double valueY, double stepSizeY) {
        super(x, y, Math.max(2, width), Math.max(height, 2), Component.empty(), 0D);
        // X-Axis values
        this.minValueX = minValueX;
        this.maxValueX = maxValueX;
        this.value = this.snapToNearestX((valueX - minValueX) / (maxValueX - minValueX));
        this.stepSizeX = stepSizeX;
        // Y-Axis values
        this.minValueY = minValueY;
        this.maxValueY = maxValueY;
        this.valueY = this.snapToNearestY((valueY - minValueY) / (maxValueY - minValueY));
        this.stepSizeY = stepSizeY;
        // Handle values
        this.handleWidth = 8;
        this.handleHeight = 8;

        this.interfix = Component.literal(", ");// Default interfix to make x and y easier to read.
        String pattern = !Mth.equal(this.stepSizeX, Mth.floor(this.stepSizeX)) ? Double.toString(this.stepSizeX).replaceAll("\\d", "0") : "0";
        this.formatX = new DecimalFormat(pattern);
        pattern = !Mth.equal(this.stepSizeY, Mth.floor(this.stepSizeY)) ? Double.toString(this.stepSizeY).replaceAll("\\d", "0") : "0";
        this.formatY = new DecimalFormat(pattern);
        this.updateMessage();
    }

    /**
     * Allows to hide/show the text of this slider.
     * @param isTextHidden Whether the slider text should be hidden
     */
    public PaCo2DSlider setTextHidden(boolean isTextHidden) {
        this.isTextHidden = isTextHidden;
        this.updateMessage();
        return this;
    }

    /**
     * Adds the given {@link Component} as a prefix, to the text of the slider.<br/>
     * Note: <strong>null</strong> can be passed to hide the prefix.
     * @param prefix The {@link Component} that will be used as the prefix
     */
    public PaCo2DSlider setPrefix(@Nullable Component prefix) {
        this.prefix = prefix;
        this.updateMessage();
        return this;
    }

    /**
     * Adds the given {@link Component} as an interfix between the values, to the text of the slider.<br/>
     * Note: <strong>null</strong> can be passed to hide the interfix.
     * @param interfix The {@link Component} that will be used as the interfix
     */
    public PaCo2DSlider setInterfix(@Nullable Component interfix) {
        this.interfix = interfix;
        this.updateMessage();
        return this;
    }

    /**
     * Adds the given {@link Component} as a suffix, to the text of the slider.<br/>
     * Note: <strong>null</strong> can be passed to hide the suffix.
     * @param suffix The {@link Component} that will be used as the suffix
     */
    public PaCo2DSlider setSuffix(@Nullable Component suffix) {
        this.suffix = suffix;
        this.updateMessage();
        return this;
    }

    /**
     * Used to prevent the click sound from playing, when the mouse is released over the slider.
     * @param isSilent Whether to prevent the click sound
     */
    public PaCo2DSlider setSilent(boolean isSilent) {
        this.isSilent = isSilent;
        return this;
    }

    /**
     * Used to alter the sliders handle width.<br/>
     * Note: the minimum width is 2.
     * @param width The width the slider handle should have, in pixels
     */
    public PaCo2DSlider setHandleWidth(int width) {
        width = Math.max(2, width);
        this.handleWidth = width;
        return this;
    }

    /**
     * Used to alter the sliders handle height.<br/>
     * Note: the minimum height is 2.
     * @param height The height the slider handle should have, in pixels
     */
    public PaCo2DSlider setHandleHeight(int height) {
        height = Math.max(2, height);
        this.handleHeight = height;
        return this;
    }

    /**
     * Used to alter the sliders handle size.<br/>
     * Note: the minimum width and height is 2.
     * @param width The width the slider handle should have, in pixels
     * @param height The height the slider handle should have, in pixels
     */
    public PaCo2DSlider setHandleSize(int width, int height) {
        return this.setHandleWidth(width).setHandleHeight(height);
    }

    //TODO redo javadoc
    public PaCo2DSlider setSliderTexture(@NotNull ResourceLocation texture, int u, int v, int highlightedU, int highlightedV, int textureWidth, int textureHeight, int sliceSize) {
        return this.setSliderTexture(texture, u, v, texture, highlightedU, highlightedV, textureWidth, textureHeight, sliceSize);
    }

    //TODO redo javadoc
    public PaCo2DSlider setSliderTexture(@NotNull ResourceLocation texture, int u, int v, @NotNull ResourceLocation highlightedTexture, int highlightedU, int highlightedV, int textureWidth, int textureHeight, int sliceSize) {
        this.sliderTexture = texture;
        this.sliderU = u;
        this.sliderV = v;
        this.sliderHighlightedTexture = highlightedTexture;
        this.sliderHighlightedU = highlightedU;
        this.sliderHighlightedV = highlightedV;
        this.sliderTextureWidth = textureWidth;
        this.sliderTextureHeight = textureHeight;
        this.sliderSliceSize = sliceSize;
        return this;
    }

    /**
     * Used to specify colors that should be used to render the slider and its rim.<br/>
     * Note: This replaces the slider rendering from using a texture to creating a color based blit,
     * meaning that it makes it impossible for resource packs to modify the slider, so use it responsibly.<br/>
     * Additionally, {@link PaCoColor} can be used to easily create a color.<br/>
     * If null is given the element won't be rendered, however at least one has to be not null, to fully hide use alpha 0.
     * @param sliderColor The main color the slider will have
     * @param sliderRimColor The rim color the slider will have
     * @param sliderHighlightedRimColor The highlighted rim color the slider will have
     */
    public PaCo2DSlider setSliderColor(@Nullable Integer sliderColor, @Nullable Integer sliderRimColor, @Nullable Integer sliderHighlightedRimColor) {
        this.sliderColor = sliderColor;
        this.sliderRimColor = sliderRimColor;
        this.sliderHighlightedRimColor = sliderHighlightedRimColor;
        return this;
    }

    //TODO redo javadoc
    public PaCo2DSlider setHandleTexture(@NotNull ResourceLocation texture, int u, int v, int highlightedU, int highlightedV, int textureWidth, int textureHeight, int sliceSize) {
        return this.setHandleTexture(texture, u, v, texture, highlightedU, highlightedV, textureWidth, textureHeight, sliceSize);
    }

    //TODO redo javadoc
    public PaCo2DSlider setHandleTexture(@NotNull ResourceLocation texture, int u, int v, @NotNull ResourceLocation highlightedTexture, int highlightedU, int highlightedV, int textureWidth, int textureHeight, int sliceSize) {
        this.handleTexture = texture;
        this.handleU = u;
        this.handleV = v;
        this.handleHighlightedTexture = highlightedTexture;
        this.handleHighlightedU = highlightedU;
        this.handleHighlightedV = highlightedV;
        this.handleTextureWidth = textureWidth;
        this.handleTextureHeight = textureHeight;
        this.handleSliceSize = sliceSize;
        return this;
    }

    /**
     * Used to specify colors that should be used to render the slider handle and its rim.<br/>
     * Note: This replaces the slider handle rendering from using a texture to creating a color based blit,
     * meaning that it makes it impossible for resource packs to modify the slider handle, so use it responsibly.<br/>
     * Additionally, {@link PaCoColor} can be used to easily create a color.<br/>
     * If null is given the element won't be rendered, however at least one has to be not null, to fully hide use alpha 0.
     * @param handleColor The main color the slider handle will have
     * @param handleRimColor The rim color the slider handle will have
     * @param handleHighlightedRimColor The highlighted rim color the slider handle will have
     */
    public PaCo2DSlider setHandleColor(@Nullable Integer handleColor, @Nullable Integer handleRimColor, @Nullable Integer handleHighlightedRimColor) {
        this.handleColor = handleColor;
        this.handleRimColor = handleRimColor;
        this.handleHighlightedRimColor = handleHighlightedRimColor;
        return this;
    }

    /**
     * Used to set the text color, this slider should have.<br/>
     * This only sets the "{@link PaCo2DSlider#active}" color, if the slider can be deactivated use: {@link PaCo2DSlider#setTextColor(int, int)} <br/>
     * This ignores the alpha value of the given color, as the text is only affected by the {@link PaCo2DSlider#alpha} value of the slider.
     * @param textColor The color the text will have, {@link PaCoColor} can be used.
     */
    public PaCo2DSlider setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    /**
     * Used to set the text color, this slider should have.<br/>
     * If the slider is not "{@link PaCo2DSlider#active}" the second given color will be used.<br/>
     * This ignores the alpha value of the given color, as the text is only affected by the {@link PaCo2DSlider#alpha} value of the slider.
     * @param textColor The color the text will have, {@link PaCoColor} can be used.
     * @param textColorInactive The color the text will have if the slider is not "{@link PaCo2DSlider#active}", {@link PaCoColor} can be used.
     */
    public PaCo2DSlider setTextColor(int textColor, int textColorInactive) {
        this.textColor = textColor;
        this.textColorInactive = textColorInactive;
        return this;
    }

    /**
     * Used to offset the slider text by x and y pixels.
     * @param offsetX The x offset, negative values move the text to the left, and positive values to the right
     * @param offsetY The y offset, negative values move the text up, and positive values down
     */
    public PaCo2DSlider setTextOffset(int offsetX, int offsetY) {
        this.textOffsetX = offsetX;
        this.textOffsetY = offsetY;
        return this;
    }

    /**
     * Used to specify a position the text will snap too horizontally.<br/>
     * Note: this can be combined with<br/>
     * {@link PaCo2DSlider#setVerticalTextSnap(VerticalTextSnap)} and {@link PaCo2DSlider#setTextOffset(int, int)}.
     * @param horizontalTextSnap The {@link HorizontalTextSnap} position the text will snap to
     */
    public PaCo2DSlider setHorizontalTextSnap(HorizontalTextSnap horizontalTextSnap) {
        this.horizontalTextSnap = horizontalTextSnap;
        return this;
    }

    /**
     * Used to specify a position the text will snap too vertically.<br/>
     * Note: this can be combined with<br/>
     * {@link PaCo2DSlider#setHorizontalTextSnap(HorizontalTextSnap)} and {@link PaCo2DSlider#setTextOffset(int, int)}.
     * @param verticalTextSnap The {@link VerticalTextSnap} position the text will snap to
     */
    public PaCo2DSlider setVerticalTextSnap(VerticalTextSnap verticalTextSnap) {
        this.verticalTextSnap = verticalTextSnap;
        return this;
    }

    /**
     * Used to specify the horizontal and vertical positions the text will snap too.<br/>
     * Note: this can be combined with {@link PaCo2DSlider#setTextOffset(int, int)}.
     * @param horizontalTextSnap The {@link HorizontalTextSnap} position the text will snap to
     * @param verticalTextSnap The {@link VerticalTextSnap} position the text will snap to
     */
    public PaCo2DSlider setTextSnap(HorizontalTextSnap horizontalTextSnap, VerticalTextSnap verticalTextSnap) {
        return this.setHorizontalTextSnap(horizontalTextSnap).setVerticalTextSnap(verticalTextSnap);
    }

    /**
     * Used to specify whether the slider text should render with a drop shadow.
     * @param dropShadow Renders with drop shadow
     */
    public PaCo2DSlider setHasDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    /**
     * Used to pad the sliders text with zeros.<br/>
     * Basically what this does is, it gets the x-axis min/max and y-axis min/max values, and checks which of
     * the two has more digits and stores the result, it then uses those values to determine how many zeros to
     * place before the current x-axis and y-axis values, to make them match the digit count.<br/>
     * So for example, on a slider of range 0-1000 if the current value is "32", it would instead display "0032".
     * @param zeroPad Whether to pad the sliders text with zeros
     */
    public PaCo2DSlider setZeroPadding(boolean zeroPad) {
        this.zeroPad = zeroPad;
        this.updateMessage();
        return this;
    }

    /** @return The current x-axis value of the slider. */
    public double getValueX() {
        return this.value * (this.maxValueX - this.minValueX) + this.minValueX;
    }

    /** @return The current y-axis value of the slider. */
    public double getValueY() {
        return this.valueY * (this.maxValueY - this.minValueY) + this.minValueY;
    }

    /**
     * Used to set the x-axis value of the slider.
     * @param valueX The value the slider should be set to
     */
    public void setValueX(double valueX) {
        this.value = this.snapToNearestX((valueX - this.minValueX) / (this.maxValueX - this.minValueX));
        this.updateMessage();
    }

    /**
     * Used to set the y-axis value of the slider.
     * @param valueY The value the slider should be set to
     */
    public void setValueY(double valueY) {
        this.valueY = this.snapToNearestY((valueY - this.minValueY) / (this.maxValueY - this.minValueY));
        this.updateMessage();
    }

    /** @return The current x-axis value of the slider as a string. */
    public String getAsStringX() {
        return this.formatX.format(this.getValueX());
    }

    /** @return The current y-axis value of the slider as a string. */
    public String getAsStringY() {
        return this.formatY.format(this.getValueY());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseX, mouseY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (!this.isSilent) // Prevent click sound if needed
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
            boolean isLeftKey = keyCode == GLFW.GLFW_KEY_LEFT;
            boolean isRightKey = keyCode == GLFW.GLFW_KEY_RIGHT;
            boolean isUpKey = keyCode == GLFW.GLFW_KEY_UP;
            boolean isDownKey = keyCode == GLFW.GLFW_KEY_DOWN;
            if (isLeftKey || isRightKey) {
                int direction = (isLeftKey ? -1 : 1) * (this.minValueX > this.maxValueX ? -1 : 1);
                this.setValueX(this.getValueX() + (this.stepSizeX * direction));
                return true;
            }
            if (isUpKey || isDownKey) {
                int direction = (isUpKey ? -1 : 1) * (this.minValueY > this.maxValueY ? -1 : 1);
                this.setValueY(this.getValueY() + (this.stepSizeY * direction));
                return true;
            }
        }
        // If the slider wasn't "selected" we just return as we don't want to modify its value.
        return false;
    }

    protected void setValueFromMouse(double mouseX, double mouseY) {
        this.setSliderValueX((mouseX - (double)(this.getX() + (this.handleWidth / 2F))) / (double)(this.width - this.handleWidth));
        this.setSliderValueY((mouseY - (double)(this.getY() + (this.handleHeight / 2F))) / (double)(this.height - this.handleHeight));
    }

    protected void setSliderValueX(double valueX) {
        double oldValueX = this.value;
        this.value = this.snapToNearestX(valueX);
        if (!Mth.equal(oldValueX, this.value))
            this.applyValue();
        this.updateMessage();
    }

    protected void setSliderValueY(double valueY) {
        double oldValueY = this.valueY;
        this.valueY = this.snapToNearestY(valueY);
        if (!Mth.equal(oldValueY, this.valueY))
            this.applyValue();
        this.updateMessage();
    }

    protected double snapToNearestX(double valueX) {
        if (this.stepSizeX <= 0.0) return Mth.clamp(valueX, 0.0, 1.0);

        valueX = Mth.lerp(Mth.clamp(valueX, 0.0, 1.0), this.minValueX, this.maxValueX);
        valueX = this.stepSizeX * Math.round(valueX / this.stepSizeX);

        double minClamp = Math.min(this.minValueX, this.maxValueX);
        double maxClamp = Math.max(this.minValueX, this.maxValueX);
        valueX = Mth.clamp(valueX, minClamp, maxClamp);

        return Mth.map(valueX, this.minValueX, this.maxValueX, 0.0, 1.0);
    }

    protected double snapToNearestY(double valueY) {
        if (this.stepSizeY <= 0.0) return Mth.clamp(valueY, 0.0, 1.0);

        valueY = Mth.lerp(Mth.clamp(valueY, 0.0, 1.0), this.minValueY, this.maxValueY);
        valueY = this.stepSizeY * Math.round(valueY / this.stepSizeY);

        double minClamp = Math.min(this.minValueY, this.maxValueY);
        double maxClamp = Math.max(this.minValueY, this.maxValueY);
        valueY = Mth.clamp(valueY, minClamp, maxClamp);

        return Mth.map(valueY, this.minValueY, this.maxValueY, 0.0, 1.0);
    }

    @Override
    protected void updateMessage() {
        MutableComponent component = Component.literal("");
        if (!this.isTextHidden) {
            // Prefix
            if (this.prefix != null) component.append(this.prefix);
            // Value X
            component.append(this.zeroPad ? this.zeroPadString(this.getAsStringX(), this.minValueX, this.maxValueX, this.getValueX()) : this.getAsStringX());
            // Interfix
            if (this.interfix != null) component.append(this.interfix);
            // Value Y
            component.append(this.zeroPad ? this.zeroPadString(this.getAsStringY(), this.minValueY, this.maxValueY, this.getValueY()) : this.getAsStringY());
            // Suffix
            if (this.suffix != null) component.append(this.suffix);
        }
        this.setMessage(component);
    }

    /**
     * Pads the given string with zeros, to match the digit length of minValue/maxValue (based on which is longer).
     * @return The padded value string.
     */
    protected String zeroPadString(String str, double minValue, double maxValue, double value) {
        int commaIndex = str.indexOf(',');
        String integerPart = commaIndex != -1 ? str.substring(0, commaIndex) : str;
        String fractionalPart = commaIndex != -1 ? str.substring(commaIndex) : "";
        int maxDigits = (int) Math.log10(Math.max(Math.abs(minValue), Math.abs(maxValue))) + 1;
        boolean isNegative = value < 0;

        // Remove the '-' to prepare for padding if needed.
        if (isNegative)
            integerPart = integerPart.substring(1);
        // Zero pads the integer part if needed.
        if (integerPart.length() < maxDigits)
            integerPart = "0".repeat(maxDigits - integerPart.length()) + integerPart;
        // Reattaches the negative sign if the value is negative
        if (isNegative)
            integerPart = "-" + integerPart;

        return integerPart + fractionalPart;
    }

    @Override
    protected void applyValue() {}

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        boolean mouseOverSlider = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        boolean mouseOverHandle = false;
        // Only check if the handle is bigger than the slider
        if (this.handleWidth > this.width || this.handleHeight > this.height) {
            int posX = this.getHandleX();
            int posY = this.getHandleY();
            mouseOverHandle = mouseX >= posX && mouseY >= posY && mouseX < posX + this.handleWidth && mouseY < posY + this.handleHeight;
        }
        return this.active && this.visible && (mouseOverSlider || mouseOverHandle);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            if (this.handleWidth > this.width || this.handleHeight > this.height) {
                int posX = this.getHandleX();
                int posY = this.getHandleY();
                this.isHovered = this.isHovered || mouseX >= posX && mouseY >= posY && mouseX < posX + this.handleWidth && mouseY < posY + this.handleHeight;
            }
            this.renderWidget(graphics, mouseX, mouseY, pPartialTick);
            ((IPaCoUpdateTooltip) this).pandoraCore$updateTooltip();
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        // Slider
        this.renderSlider(graphics);
        // Slider Handle
        this.renderSliderhandle(graphics);

        int color = this.active ?
                (this.textColor != null ? this.textColor : 16777215) :
                (this.textColorInactive != null ? this.textColorInactive : 10526880);
        int textPosX = this.textOffsetX;
        int textPosY = this.textOffsetY;

        switch (this.horizontalTextSnap) {
            default -> textPosX += this.getX() + this.getWidth() / 2;
            case LEFT_INSIDE -> textPosX += this.getX();
            case LEFT_OUTSIDE -> textPosX += this.getX() - mc.font.width(this.getMessage()) + (this.dropShadow ? 0 : 1);
            case RIGHT_INSIDE -> textPosX += this.getX() + this.getWidth() - mc.font.width(this.getMessage()) + (this.dropShadow ? 0 : 1);
            case RIGHT_OUTSIDE -> textPosX += this.getX() + this.getWidth();
        }
        switch (this.verticalTextSnap) {
            default -> textPosY += this.getY() + this.getHeight() / 2 - 4; // We subtract 4 because text has a height of 8 pixels (+1 with drop shadow), and we want it centered.
            case TOP_INSIDE -> textPosY += this.getY();
            case TOP_OUTSIDE -> textPosY += this.getY() - (this.dropShadow ? 9 : 8);
            case BOTTOM_INSIDE -> textPosY += this.getY() + this.getHeight() - (this.dropShadow ? 9 : 8);
            case BOTTOM_OUTSIDE -> textPosY += this.getY() + this.getHeight();
        }

        if (this.horizontalTextSnap != HorizontalTextSnap.CENTER) {
            graphics.drawString(mc.font, this.getMessage(), textPosX, textPosY, color, this.dropShadow);
        } else {
            PaCoGuiUtils.drawCenteredString(graphics, mc.font, this.getMessage(), textPosX, textPosY, color, this.dropShadow);
        }
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Helper method to render the slider, mostly exists to allow for easy modification in children */
    protected void renderSlider(GuiGraphics graphics) {
        // Texture Based Slider
        if (this.sliderTexture != null && this.sliderHighlightedTexture != null) {
            boolean highlight = this.shouldHighlight();
            ResourceLocation texture = highlight ? this.sliderHighlightedTexture : this.sliderTexture;
            int u = highlight ? this.sliderHighlightedU : this.sliderU;
            int v = highlight ? this.sliderHighlightedV : this.sliderV;
            if (this.sliderSliceSize != 0) {
                graphics.blitNineSliced(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.sliderSliceSize, this.sliderTextureWidth, this.sliderTextureHeight, u, v);
            } else { // If slice size is 0 we render the texture as is.
                graphics.blit(texture, this.getX(), this.getY(), u, v, this.getWidth(), this.getHeight());
            }
        // Color Based Slider
        } else if (this.sliderColor != null || this.sliderRimColor != null || this.sliderHighlightedRimColor != null) {
            Integer rimColor = this.shouldHighlight() ? this.sliderHighlightedRimColor : this.sliderRimColor;
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.sliderColor, rimColor, 1);
        // Vanilla Textures Slider
        } else {
            graphics.blitNineSliced(SLIDER_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getSliderTextureY());
        }
    }

    /** Helper method to render the slider handle, mostly exists to allow for easy modification in children */
    protected void renderSliderhandle(GuiGraphics graphics) {
        int posX = this.getHandleX();
        int posY = this.getHandleY();
        // Texture Based Slider Handle
        if (this.handleTexture != null && this.handleHighlightedTexture != null) {
            boolean highlight = this.shouldHighlightHandle();
            ResourceLocation texture = highlight ? this.handleHighlightedTexture : this.handleTexture;
            int u = highlight ? this.handleHighlightedU : this.handleU;
            int v = highlight ? this.handleHighlightedV : this.handleV;
            if (this.handleSliceSize != 0) {
                graphics.blitNineSliced(texture, posX, posY, this.handleWidth, this.handleHeight, this.handleSliceSize, this.handleTextureWidth, this.handleTextureHeight, u, v);
            } else { // If slice size is 0 we render the texture as is.
                graphics.blit(texture, posX, posY, u, v, this.handleWidth, this.handleHeight);
            }
        // Color Based Slider Handle
        } else if (this.handleColor != null || this.handleRimColor != null || this.handleHighlightedRimColor != null) {
            Integer rimColor = this.shouldHighlightHandle() ? this.handleHighlightedRimColor : this.handleRimColor;
            PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, this.handleWidth, this.handleHeight, this.handleColor, rimColor, 1);
        // Vanilla Textures Slider Handle
        } else {
            graphics.blitNineSliced(SLIDER_LOCATION, posX, posY, this.handleWidth, this.handleHeight, 20, 4, 200, 20, 0, this.getSliderHandleTextureY());
        }
    }

    /** @return The x position of the handles upper left corner. */
    public int getHandleX() {
        return this.getX() + (int)(this.value * (double)(this.width - this.handleWidth));
    }

    /** @return The y position of the handles upper left corner. */
    public int getHandleY() {
        return this.getY() + (int)(this.valueY * (double)(this.height - this.handleHeight));
    }

    protected int getSliderTextureY() {
        return this.shouldHighlight() ? 20 : 0;
    }

    protected int getSliderHandleTextureY() {
        return this.shouldHighlightHandle() ? 60 : 40;
    }

    /** @return Whether the slider should be highlighted. */
    protected boolean shouldHighlight() {
        return this.isFocused() && !this.canChangeValue;
    }

    /** @return Whether the slider handle should be highlighted. */
    protected boolean shouldHighlightHandle() {
        return this.isHovered || this.canChangeValue;
    }
}